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
import com.poolcabs.model.datatype.DragDropItem;
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
import org.icefaces.ace.event.SelectEvent;
import org.icefaces.ace.event.UnselectEvent;
import org.icefaces.ace.model.table.RowStateMap;

/**
 *
 * @author Administrator
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
        bookingsToBeBooked.clear();
        bookingsToBeBooked.addAll(stateMap.getSelected());
        bookingListForm = false;
        bookCabFormRendered = true;
    }

    public void renderEditBookingForm() {
        bookingsToBeBooked.clear();
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
        renderbookingListForm();
    }

    public void update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("BookingUpdated"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    public void cancelCabBooking() {
        undoSelection();
        renderbookingListForm();
    }

    public List<CabStatus> cabStatuses() {
        return CabStatus.list();
    }

    public void rowSelected(SelectEvent bookingSelectEvent) {
        current = (Booking) bookingSelectEvent.getObject();
    }

    public void rowUnselected(UnselectEvent bookingUnselectEvent) {
        current = null;
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
}