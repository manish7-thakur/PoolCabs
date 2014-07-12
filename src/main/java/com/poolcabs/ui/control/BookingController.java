package com.poolcabs.ui.control;

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
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import org.icefaces.ace.event.DateSelectEvent;
import org.icefaces.ace.event.DateTextChangeEvent;
import org.icefaces.ace.event.DragDropEvent;

@ManagedBean(name = "bookingController")
@ViewScoped
public class BookingController implements Serializable {

    public static final int TAG_TIME = 3;
    private Booking current;
    private DataModel items = null;
    @EJB
    private com.poolcabs.dao.BookingFacade ejbFacade;
    @EJB
    private UserFacade userFacade;
    @EJB
    private TariffFacade tariffFacade;
    @EJB
    private BookingEmailMessageService bookingEmailMessageService;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private boolean googleMapRendered;
    private boolean bookingFormRendered;
    private boolean emailFormRendered;
    private String bookingType;
    private Date rideStartTime;
    List<Booking> itemizedBookingList;
    private Date minimumDateForCalender;
    private Date minimumDateForRideEndDate;
    private Tariff tariff;
    private List<DragDropItem> userAddressList;
    User currentUser;
    private Integer hour;
    private Integer returnHour;
    private String guestEmail;
    private Map<String, Integer> timeWindowMap;

    public BookingController() {
    }

    @PostConstruct
    public void init() {
        googleMapRendered = false;
        bookingFormRendered = true;
        emailFormRendered = false;

        userAddressList = new ArrayList<DragDropItem>();
        tariff = tariffFacade.findAll().get(0);
        timeWindowMap = new TreeMap<String, Integer>();
        currentUser = (User) getSessionMap().get("user");
        if (null != currentUser) {
            int i = 0;
            for (String address : currentUser.getAddressSet()) {
                DragDropItem item = new DragDropItem(++i, address);
                userAddressList.add(item);
            }
            getSelected().setCustomerName(currentUser.getName());
            getSelected().setMobileNumber(currentUser.getMobileNumber());
        }
        bookingType = (String) getSessionMap().get("bookingType");
        if (null == bookingType) {
            try {
                getExternalContext().redirect(getExternalContext().getRequestContextPath() + "/products.jsf");
            } catch (IOException ex) {
                Logger.getLogger(BookingController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (null != bookingType && bookingType.equalsIgnoreCase(BookingType.INSTANT.getValue())) {
            Calendar calender = Calendar.getInstance();
            calender.set(Calendar.HOUR_OF_DAY, 0);
            calender.set(Calendar.MINUTE, 0);
            calender.set(Calendar.SECOND, 0);
            calender.set(Calendar.MILLISECOND, 0);
            getSelected().setRideStartDate(calender.getTime());
        }
        if (null != bookingType && (bookingType.equalsIgnoreCase(BookingType.CASUAL.getValue()) || bookingType.equalsIgnoreCase(BookingType.PERSONAL.getValue()))) {
            Calendar calender = Calendar.getInstance();
            calender.add(Calendar.HOUR_OF_DAY, TAG_TIME);
            minimumDateForCalender = calender.getTime();
            populateTimeWindowMap(calender.get(Calendar.HOUR_OF_DAY));
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

            saveNewAddressForUser(current);

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
        Calendar calendar = Calendar.getInstance();
        if (null != rideStartTime) {
            selectedDate.setTime(rideStartTime);
        }
        calendar.setTime(current.getRideStartDate());
        if (bookingType.equalsIgnoreCase(BookingType.CASUAL.getValue())) {
            calendar.set(calendar.HOUR_OF_DAY, hour);
        } else {
            calendar.set(calendar.HOUR_OF_DAY, selectedDate.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, selectedDate.get(Calendar.MINUTE));
            calendar.set(Calendar.SECOND, 0);
        }
        current.setPickupTime(calendar.getTime());
    }

    public void valueChanged(ValueChangeEvent event) {
        current.setDistanceInKM((Double) event.getNewValue());
    }

    public void cancelUpdate() {
        current = null;
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

    public boolean isEmailFormRendered() {
        return emailFormRendered;
    }

    public void setEmailFormRendered(boolean emailFormRendered) {
        this.emailFormRendered = emailFormRendered;
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

    public List<Booking> getItemizedBookingList() {
        return itemizedBookingList;
    }

    public void setItemizedBookingList(List<Booking> itemizedBookingList) {
        this.itemizedBookingList = itemizedBookingList;
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

    public boolean isBookingFormRendered() {
        return bookingFormRendered;
    }

    public void setBookingFormRendered(boolean bookingFormRendered) {
        this.bookingFormRendered = bookingFormRendered;
    }

    private List<Booking> createIndividualBookings(Booking current) {
        List<Booking> bookings = new ArrayList<Booking>();
        bookings.add(current);
        Booking clone = null;
        Calendar startInstance = Calendar.getInstance();
        Calendar endInstance = Calendar.getInstance();
        startInstance.setTime(current.getPickupTime());
        endInstance.setTime(current.getRideEndDate());
        if (startInstance.compareTo(endInstance) == 0) {
            return bookings;
        }
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
                //instance.set(Calendar.HOUR_OF_DAY, returnClone.getReturnPickUpTime().getHours());
                //instance.set(Calendar.MINUTE, returnClone.getReturnPickUpTime().getMinutes());
                if (bookingType.equalsIgnoreCase(BookingType.CASUAL.getValue())) {
                    instance.set(Calendar.HOUR_OF_DAY, returnHour);
                    instance.set(Calendar.MINUTE, 0);
                    instance.set(Calendar.SECOND, 0);
                } else {
                    instance.setTime(getSelected().getPickupTime());
                    instance.set(Calendar.HOUR_OF_DAY, getSelected().getReturnPickUpTime().getHours());
                    instance.set(Calendar.MINUTE, getSelected().getReturnPickUpTime().getMinutes());
                    instance.set(Calendar.SECOND, 0);
                }
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
        redirectToRegistrationPage();
        bookingEmailMessageService.sendMail(itemizedBookingList, guestEmail.split(","));
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
        if (bookingType.equalsIgnoreCase(BookingType.CASUAL.getValue()) || bookingType.equalsIgnoreCase(BookingType.PERSONAL.getValue())) {
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

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    private void saveNewAddressForUser(Booking current) {
        if (null != currentUser) {
            currentUser.getAddressSet().add(getSelected().getPickupStreetAddress());
            currentUser.getAddressSet().add(getSelected().getDropStreetAddress());
            User pesistedUser = null;
            try {
                pesistedUser = userFacade.edit(currentUser);
            } catch (Exception e) {
                Logger.getLogger(BookingController.class.getName()).log(Level.SEVERE, null, e);
            }
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", pesistedUser);
        }
    }

    public Integer getReturnHour() {
        return returnHour;
    }

    public void setReturnHour(Integer returnHour) {
        this.returnHour = returnHour;
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
                booking.setTotalCost(booking.getDistanceInKM() != null ? booking.getDistanceInKM() * booking.getTarrif() : null);
            } else if (booking.getBookingType().getValue().equals(BookingType.CASUAL.getValue())) {
                booking.setTarrif(tariff.getCasualTariff());
                booking.setTotalCost(booking.getDistanceInKM() != null ? booking.getDistanceInKM() * booking.getTarrif() : null);
            } else if (booking.getBookingType().getValue().equals(BookingType.INSTANT.getValue())) {
                booking.setTarrif(tariff.getInstantTariff());
                booking.setTotalCost(booking.getDistanceInKM() != null ? booking.getDistanceInKM() * booking.getTarrif() : null);
            } else if (booking.getBookingType().getValue().equals(BookingType.OUTSTATION.getValue())) {
                booking.setTarrif(tariff.getOutstationTariff());
                booking.setTotalCost(booking.getDistanceInKM() != null ? booking.getDistanceInKM() * booking.getTarrif() : null);
            } else if (booking.getBookingType().getValue().equals(BookingType.PERSONAL.getValue())) {
                booking.setTarrif(tariff.getPersonalTariff());
                booking.setTotalCost(booking.getDistanceInKM() != null ? booking.getDistanceInKM() * booking.getTarrif() : null);
            }
        }
    }

    private void setMinHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        Calendar current = Calendar.getInstance();
        Calendar minimumDateForCalenderInstance = Calendar.getInstance();
        minimumDateForCalenderInstance.setTime(minimumDateForCalender);
        calendar.setTime(date);
        if (calendar.after(minimumDateForCalenderInstance)) {
            hour = 12;
            populateTimeWindowMap(0);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, current.get(Calendar.HOUR_OF_DAY));
            calendar.add(Calendar.HOUR_OF_DAY, TAG_TIME);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            populateTimeWindowMap(calendar.get(Calendar.HOUR_OF_DAY));
        }
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Map<String, Integer> getTimeWindowMap() {
        return timeWindowMap;
    }

    public void setTimeWindowMap(Map<String, Integer> timeWindowMap) {
        this.timeWindowMap = timeWindowMap;
    }

    private void populateTimeWindowMap(int minHourApplicable) {
        timeWindowMap.clear();
        while (minHourApplicable <= 23) {
            timeWindowMap.put(String.format("%02d", minHourApplicable) + ":" + "00" + " - " + String.format("%02d", minHourApplicable) + ":" + 59 + " " + "Hrs", minHourApplicable);
            minHourApplicable++;
        }
    }
}
