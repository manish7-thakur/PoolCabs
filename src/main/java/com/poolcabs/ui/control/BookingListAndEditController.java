/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.ui.control;

import com.poolcabs.bookingservice.BookingService;
import com.poolcabs.dao.BookingFacade;
import com.poolcabs.dao.util.JsfUtil;
import com.poolcabs.model.Booking;
import com.poolcabs.model.CabStatus;
import com.poolcabs.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.icefaces.ace.event.RowEditEvent;
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.event.UnselectEvent;
import org.icefaces.ace.model.table.RowStateMap;

/**
 *
 * @author Manish
 */
@ManagedBean
@ViewScoped
public class BookingListAndEditController {

    private boolean bookingListForm;
    private boolean bookCabFormRendered;
    private List<Booking> bookingsToBeBooked;
    private boolean editBookingFormRendered;
    @EJB
    private BookingService bookingService;
    @EJB
    private com.poolcabs.dao.BookingFacade ejbFacade;
    private RowStateMap stateMap;
    private Booking current;
    private List<Booking> userBookings;
    private User currentUser;
    private List<Booking> bookingList;

    @PostConstruct
    public void init() {
        bookingListForm = true;
        bookCabFormRendered = false;
        bookingsToBeBooked = new ArrayList<Booking>();
        editBookingFormRendered = false;
        stateMap = new RowStateMap();
        bookingList = ejbFacade.findAll();
        currentUser = (User) getSessionMap().get("user");
        if (null != currentUser) {
            userBookings = ejbFacade.findAllByPhoneNumber(currentUser.getMobileNumber());
        }
    }

    public Booking getSelected() {
        if (current == null) {
            current = new Booking();
        }
        return current;
    }

    private Map<String, Object> getSessionMap() {
        return getExternalContext().getSessionMap();
    }

    private ExternalContext getExternalContext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }

    public void renderCabBookingForm() {
        clearBookingList();
        bookingsToBeBooked.addAll(stateMap.getSelected());
        bookingListForm = false;
        bookCabFormRendered = true;
    }

    public void renderEditBookingForm() {
        current = (Booking) bookingsToBeBooked.get(0);
        clearBookingList();
        bookingListForm = false;
        bookCabFormRendered = false;
        editBookingFormRendered = true;
    }

    public void renderbookingListForm() {
        bookingListForm = true;
        bookCabFormRendered = false;
        editBookingFormRendered = false;
    }

    public void updateAndRenderListForm() {
        update();
        renderbookingListForm();
        clearBookingList();
        undoSelection();
    }

    public void bookCab() {
        try {
            bookingService.updateBookings(bookingsToBeBooked);
            bookingService.informAllThroughMessage(bookingsToBeBooked);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("BookingUpdated"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
        clearBookingList();
        undoSelection();
        renderbookingListForm();
    }

    public void update() {
        try {
            getFacade().edit(getSelected());
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("BookingUpdated"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    public void update(RowEditEvent event) {
        try {
            getFacade().edit((Booking) event.getObject());
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("BookingUpdated"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void performDestroy(Booking booking) {
        try {
            getFacade().remove(booking);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("BookingDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    public void delete() {
        for (Booking booking : bookingsToBeBooked) {
            performDestroy(booking);
        }
        bookingList.removeAll(bookingsToBeBooked);
        clearBookingList();
        undoSelection();
    }

    public void cancel() {
        clearBookingList();
        undoSelection();
        renderbookingListForm();
    }

    public List<CabStatus> cabStatuses() {
        return CabStatus.list();
    }

    public void rowSelected(SelectEvent bookingSelectEvent) {
        bookingsToBeBooked.add((Booking) bookingSelectEvent.getObject());
    }

    public void rowUnselected(UnselectEvent bookingUnselectEvent) {
        bookingsToBeBooked.remove((Booking) bookingUnselectEvent.getObject());
    }

    private void undoSelection() {
        stateMap.setAllSelected(false);
    }

    private BookingFacade getFacade() {
        return ejbFacade;
    }

    public boolean isEditBookingForm() {
        return editBookingFormRendered;
    }

    public void setEditBookingForm(boolean editBookingForm) {
        this.editBookingFormRendered = editBookingForm;
    }

    public RowStateMap getStateMap() {
        return stateMap;
    }

    public void setStateMap(RowStateMap stateMap) {
        this.stateMap = stateMap;
    }

    public List<Booking> getBookingsToBeBooked() {
        return bookingsToBeBooked;
    }

    public void setBookingsToBeBooked(List<Booking> bookingsToBeBooked) {
        this.bookingsToBeBooked = bookingsToBeBooked;
    }

    public boolean isBookCabFormRendered() {
        return bookCabFormRendered;
    }

    public List<Booking> getUserBookings() {
        return userBookings;
    }

    public void setUserBookings(List<Booking> userBookings) {
        this.userBookings = userBookings;
    }

    public void setBookCabFormRendered(boolean bookCabFormRendered) {
        this.bookCabFormRendered = bookCabFormRendered;
    }

    public boolean isBookingListForm() {
        return bookingListForm;
    }

    public void setBookingListForm(boolean bookingListForm) {
        this.bookingListForm = bookingListForm;
    }

    public List<Booking> getBookingList() {
        return bookingList;
    }

    public void setBookingList(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    private void clearBookingList() {
        bookingsToBeBooked.clear();
    }
}
