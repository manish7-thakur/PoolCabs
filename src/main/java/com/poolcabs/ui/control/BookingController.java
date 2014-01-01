package com.poolcabs.ui.control;

import com.poolcabs.bookingservice.BookingService;
import com.poolcabs.dao.BookingFacade;
import com.poolcabs.dao.TariffFacade;
import com.poolcabs.dao.UserFacade;
import com.poolcabs.dao.util.JsfUtil;
import com.poolcabs.dao.util.PaginationHelper;
import com.poolcabs.messaging.service.BookingEmailMessageService;
import com.poolcabs.model.Booking;
import com.poolcabs.model.BookingType;
import com.poolcabs.model.CabStatus;
import com.poolcabs.model.GeoCode;
import com.poolcabs.model.Tariff;
import com.poolcabs.model.User;
import com.poolcabs.model.datatype.DragDropItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import org.icefaces.ace.event.DateSelectEvent;
import org.icefaces.ace.event.DateTextChangeEvent;
import org.icefaces.ace.event.DragDropEvent;
import org.icefaces.ace.event.RowEditEvent;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.event.UnselectEvent;
import org.icefaces.ace.model.table.RowStateMap;

@ManagedBean(name = "bookingController")
@ViewScoped
public class BookingController implements Serializable {

    private Booking current;
    private DataModel items = null;
    @EJB
    private com.poolcabs.dao.BookingFacade ejbFacade;
    @EJB
    private UserFacade userFacade;
    @EJB
    private TariffFacade tariffFacade;
    @EJB
    private BookingService bookingService;
    @EJB
    private BookingEmailMessageService bookingEmailMessageService;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private boolean googleMapRendered;
    private boolean bookingFormRendered;
    private boolean bookCabFormRendered;
    private boolean emailFormRendered;
    private String bookingType;
    private Date rideStartTime;
    private List<Booking> bookingList;
    List<Booking> itemizedBookingList;
    private Date minimumDateForCalender;
    private Date minimumDateForRideEndDate;
    private List<Booking> userBookings;
    private List<Booking> bookingsToBeBooked;
    private boolean bookingListForm;
    private Tariff tariff;
    private RowStateMap stateMap;
    private List<DragDropItem> userAddressList;
    User currentUser;
    private int hour;
    private String guestEmail;

    public BookingController() {
    }

    @PostConstruct
    public void init() {
        googleMapRendered = false;
        bookingFormRendered = true;
        bookCabFormRendered = false;
        emailFormRendered = false;
        bookingListForm = true;
        stateMap = new RowStateMap();
        bookingList = ejbFacade.findAll();
        bookingsToBeBooked = new ArrayList<Booking>();
        userAddressList = new ArrayList<DragDropItem>();
        tariff = tariffFacade.findAll().get(0);

        currentUser = (User) getSessionMap().get("user");
        if (null != currentUser) {
            userBookings = ejbFacade.findAllByPhoneNumber(currentUser.getMobileNumber());
            int i = 0;
            for (String address : currentUser.getAddressSet()) {
                DragDropItem item = new DragDropItem(++i, address);
                userAddressList.add(item);
            }

            getSelected().setCustomerName(currentUser.getName());
            getSelected().setMobileNumber(currentUser.getMobileNumber());

        }
        bookingType = (String) getSessionMap().get("bookingType");
        if (null != bookingType && bookingType.equalsIgnoreCase("INSTANT")) {
            Calendar calender = Calendar.getInstance();
            calender.set(Calendar.HOUR_OF_DAY, 0);
            calender.set(Calendar.MINUTE, 0);
            calender.set(Calendar.SECOND, 0);
            calender.set(Calendar.MILLISECOND, 0);
            getSelected().setRideStartDate(calender.getTime());
        }
        if (null != bookingType && bookingType.equalsIgnoreCase("CASUAL")) {
            Calendar calender = Calendar.getInstance();
            calender.add(Calendar.HOUR_OF_DAY, 6);
            minimumDateForCalender = calender.getTime();
        } else {
            minimumDateForCalender = new Date();
        }
    }

    public Booking getSelected() {
        if (current == null) {
            current = new Booking();
            selectedItemIndex = -1;
        }
        return current;
    }

    private BookingFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {
                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Booking) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        //current = new Booking();
        selectedItemIndex = -1;
        return "Create";
    }

    public void renderGoogleMap() {
        googleMapRendered = true;
        bookingFormRendered = false;
        emailFormRendered = false;
    }

    public void create() {
        try {
            current.setBookingType(BookingType.valueOf(bookingType));
            current.setStatus(CabStatus.PENDING);
            current.setCreateDate(new Date());
            populateRideStartDate();
            if (null == current.getRideEndDate()) {
                current.setRideEndDate(current.getRideStartDate());
            }
            itemizedBookingList = createIndividualBookings(current);

            if (current.isRoundTrip()) {
                itemizedBookingList.addAll(createReturnBookingForEachBooking(itemizedBookingList));
            }
            populateCharges(itemizedBookingList);
            for (Booking booking : itemizedBookingList) {
                getFacade().create(booking);
            }
            try {
                saveNewAddressForUser(current);
            } catch (Exception e) {
                Logger.getLogger(BookingController.class.getName()).log(Level.SEVERE, null, e);
            }
            if (null != currentUser) {
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("BookingCreated"));
                getExternalContext().redirect(getExternalContext().getRequestContextPath() + "/booking/userbookings.jsf");
            } else {
                renderEmailForm();
            }
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            //return null;
        }
    }

    private void populateRideStartDate() {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTime(rideStartTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current.getRideStartDate());
        calendar.set(calendar.HOUR_OF_DAY, selectedDate.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, selectedDate.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, 0);
        current.setPickupTime(calendar.getTime());
    }

    public void valueChanged(ValueChangeEvent event) {
        current.setDistanceInKM((Double) event.getNewValue());
    }

    public String prepareEdit() {
        current = (Booking) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public void update(RowEditEvent bookingEditEvent) {
        try {
            current = (Booking) bookingEditEvent.getObject();
            getFacade().edit(current);
            recreateModel();
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("BookingUpdated"));
            // return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            // return null;
        }
    }

    public void bookCab() {
        try {
            bookingService.allocateCab(bookingsToBeBooked);
            bookingService.informAllThroughMessage(bookingsToBeBooked);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("BookingUpdated"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            // return null;
        }
        undoSelection();
        recreateModel();
        renderbookingListForm();
    }

    public void cancelUpdate() {
        current = null;
    }

    public void cancelCabBooking() {
        undoSelection();
        renderbookingListForm();
    }

    public String destroy() {
        // current = (Booking) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public void rowSelected(SelectEvent bookingSelectEvent) {
        bookingsToBeBooked.add((Booking) bookingSelectEvent.getObject());
    }

    public void rowUnselected(UnselectEvent bookingUnselectEvent) {
        bookingsToBeBooked.remove((Booking) bookingUnselectEvent.getObject());
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    public RowStateMap getStateMap() {
        return stateMap;
    }

    public void setStateMap(RowStateMap stateMap) {
        this.stateMap = stateMap;
    }

    public List<DragDropItem> getUserAddressList() {
        return userAddressList;
    }

    public void setUserAddressList(List<DragDropItem> userAddressList) {
        this.userAddressList = userAddressList;
    }

    public String getPoints() {
        return current.getPickupStreetAddress() + ":" + current.getDropStreetAddress();
    }

    public List<CabStatus> cabStatuses() {
        return CabStatus.list();
    }

    public boolean isEmailFormRendered() {
        return emailFormRendered;
    }

    public void setEmailFormRendered(boolean emailFormRendered) {
        this.emailFormRendered = emailFormRendered;
    }

    public void renderCabBookingForm() {
        bookingsToBeBooked.clear();
        bookingsToBeBooked.addAll(stateMap.getSelected());
        bookingListForm = false;
        bookCabFormRendered = true;
    }

    public void renderbookingListForm() {
        bookingListForm = true;
        bookCabFormRendered = false;
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("BookingDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    public void handleDrop(DragDropEvent e) {
        DragDropItem item = (DragDropItem) e.getData();
        String id = e.getDropId();
        if (id.equalsIgnoreCase("pickupStreetAddress")) {
            getSelected().setPickupStreetAddress(item.getValue());
        } else if (id.equalsIgnoreCase("dropStreetAddress")) {
            getSelected().setDropStreetAddress(item.getValue());
        }
        userAddressList.remove(item);
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    public Date getRideStartTime() {
        return rideStartTime;
    }

    public void setRideStartTime(Date rideStartTime) {
        this.rideStartTime = rideStartTime;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public List<Booking> getBookingsToBeBooked() {
        return bookingsToBeBooked;
    }

    public void setBookingsToBeBooked(List<Booking> bookingsToBeBooked) {
        this.bookingsToBeBooked = bookingsToBeBooked;
    }

    public List<Booking> getItemizedBookingList() {
        return itemizedBookingList;
    }

    public void setItemizedBookingList(List<Booking> itemizedBookingList) {
        this.itemizedBookingList = itemizedBookingList;
    }

    public void renderBookingForm() {
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public void roundTripValueChanged(ValueChangeEvent event) {
        getSelected().setRoundTrip((Boolean) event.getNewValue());
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public boolean isGoogleMapRendered() {
        return googleMapRendered;
    }

    public void setGoogleMapRendered(boolean googleMapRendered) {
        this.googleMapRendered = googleMapRendered;
    }

    public boolean isBookCabFormRendered() {
        return bookCabFormRendered;
    }

    public void setBookCabFormRendered(boolean bookCabFormRendered) {
        this.bookCabFormRendered = bookCabFormRendered;
    }

    public boolean isBookingFormRendered() {
        return bookingFormRendered;
    }

    public void setBookingFormRendered(boolean bookingFormRendered) {
        this.bookingFormRendered = bookingFormRendered;
    }

    public boolean isBookingListForm() {
        return bookingListForm;
    }

    public void setBookingListForm(boolean bookingListForm) {
        this.bookingListForm = bookingListForm;
    }

    private List<Booking> createIndividualBookings(Booking current) {
        List<Booking> bookings = new ArrayList<Booking>();
        bookings.add(current);
        Booking clone = null;
        Calendar startInstance = Calendar.getInstance();
        Calendar endInstance = Calendar.getInstance();
        startInstance.setTime(current.getPickupTime());
        endInstance.setTime(current.getRideEndDate());
        while (startInstance.compareTo(endInstance) <= 0) {
            try {
                clone = (Booking) current.clone();
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(BookingController.class.getName()).log(Level.SEVERE, null, ex);
            }
            startInstance.add(Calendar.DAY_OF_MONTH, 1);
            clone.setPickupTime(startInstance.getTime());
            bookings.add(clone);
        }
        return bookings;
    }

    private void swapLocations(Booking booking) {
        GeoCode tempCode = booking.getPickupGeoCode();
        booking.setPickupGeoCode(booking.getDropGeocode());
        booking.setDropGeocode(tempCode);
        String tempAddress = booking.getPickupStreetAddress();
        booking.setPickupStreetAddress(booking.getDropStreetAddress());
        booking.setDropStreetAddress(tempAddress);
    }

    private List<Booking> createReturnBookingForEachBooking(List<Booking> bookingList) {
        List<Booking> returnBookings = new ArrayList<Booking>();
        for (Booking booking : bookingList) {
            try {
                Booking returnClone = booking.clone();
                Calendar instance = Calendar.getInstance();
                instance.setTime(returnClone.getPickupTime());
                instance.set(Calendar.HOUR_OF_DAY, returnClone.getReturnPickUpTime().getHours());
                instance.set(Calendar.MINUTE, returnClone.getReturnPickUpTime().getMinutes());
                instance.set(Calendar.SECOND, 0);
                returnClone.setPickupTime(instance.getTime());
                swapLocations(returnClone);
                returnBookings.add(returnClone);
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(BookingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return returnBookings;
    }

    public void sendInvoiceAndRegister() {
        Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("name", getSelected().getCustomerName());
        userInfo.put("phoneNumber", getSelected().getMobileNumber());
        userInfo.put("email", guestEmail);
        getSessionMap().put("userInfo", userInfo);
        bookingEmailMessageService.sendMail(itemizedBookingList, guestEmail);
        redirectToRegistrationPage();
    }

    public List<Booking> getBookingList() {
        return bookingList;
    }

    public void setBookingList(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    public Date getMinimumDateForCalender() {
        return minimumDateForCalender;
    }

    public void setMinimumDateForCalender(Date minimumDateForCalender) {
        this.minimumDateForCalender = minimumDateForCalender;
    }

    public Date getMinimumDateForRideEndDate() {
        return minimumDateForRideEndDate;
    }

    public void setMinimumDateForRideEndDate(Date minimumDateForRideEndDate) {
        this.minimumDateForRideEndDate = minimumDateForRideEndDate;
    }

    public void dateTextChangeListener(DateTextChangeEvent event) {
        Date date = event.getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 4);
        minimumDateForRideEndDate = calendar.getTime();
    }

    public void dateSelectListener(DateSelectEvent event) {
        Date date = event.getDate();
        if (bookingType.equalsIgnoreCase(BookingType.CASUAL.getValue())) {
            setMinHour(date);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 4);
        minimumDateForRideEndDate = calendar.getTime();
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public List<Booking> getUserBookings() {
        return userBookings;
    }

    public void setUserBookings(List<Booking> userBookings) {
        this.userBookings = userBookings;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    private void undoSelection() {
        stateMap.setAllSelected(false);
    }

    private void saveNewAddressForUser(Booking current) {
        if (null != currentUser) {
            currentUser.getAddressSet().add(getSelected().getPickupStreetAddress());
            currentUser.getAddressSet().add(getSelected().getDropStreetAddress());
            userFacade.edit(currentUser);
        }
    }

    private Map<String, Object> getSessionMap() {
        return getExternalContext().getSessionMap();
    }

    private void renderEmailForm() {
        googleMapRendered = false;
        bookingFormRendered = false;
        emailFormRendered = true;
    }

    private ExternalContext getExternalContext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    private void redirectToRegistrationPage() {
        try {
            getExternalContext().redirect(getExternalContext().getRequestContextPath() + "/createuser.jsf");
        } catch (IOException ex) {
            Logger.getLogger(BookingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void populateCharges(List<Booking> itemizedBookingList) {
        for (Booking booking : itemizedBookingList) {
            if (booking.getBookingType().getValue().equals(BookingType.REGULAR.getValue())) {
                booking.setTarrif(tariff.getRegularTariff());
                booking.setTotalCost(booking.getDistanceInKM() != null ? booking.getDistanceInKM() * tariff.getRegularTariff() : null);
            } else if (booking.getBookingType().getValue().equals(BookingType.CASUAL.getValue())) {
                booking.setTarrif(tariff.getCasualTariff());
                booking.setTotalCost(booking.getDistanceInKM() != null ? booking.getDistanceInKM() * tariff.getCasualTariff() : null);
            } else if (booking.getBookingType().getValue().equals(BookingType.INSTANT.getValue())) {
                booking.setTarrif(tariff.getInstantTariff());
                booking.setTotalCost(booking.getDistanceInKM() != null ? booking.getDistanceInKM() * tariff.getInstantTariff() : null);
            } else if (booking.getBookingType().getValue().equals(BookingType.OUTSTATION.getValue())) {
                booking.setTarrif(tariff.getOutstationTariff());
                booking.setTotalCost(booking.getDistanceInKM() != null ? booking.getDistanceInKM() * tariff.getOutstationTariff() : null);
            }
        }
    }

    private void setMinHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        Calendar current = Calendar.getInstance();
        Calendar minimumDateForCalenderInstance = calendar.getInstance();
        minimumDateForCalenderInstance.setTime(minimumDateForCalender);
        calendar.setTime(date);
        if (calendar.get(Calendar.DAY_OF_MONTH) > minimumDateForCalenderInstance.get(Calendar.DAY_OF_MONTH)) {
            hour = 12;
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, current.get(Calendar.HOUR_OF_DAY));
            calendar.add(Calendar.HOUR_OF_DAY, 6);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
        }
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @FacesConverter(forClass = Booking.class)
    public static class BookingControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            BookingController controller = (BookingController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "bookingController");
            return controller.ejbFacade.find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Booking) {
                Booking o = (Booking) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Booking.class.getName());
            }
        }
    }
}
