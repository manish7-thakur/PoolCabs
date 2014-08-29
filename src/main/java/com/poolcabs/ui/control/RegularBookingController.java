package com.poolcabs.ui.control;

import com.poolcabs.dao.BookingFacade;
import com.poolcabs.dao.TariffFacade;
import com.poolcabs.dao.UserFacade;
import com.poolcabs.dao.util.JsfUtil;
import com.poolcabs.messaging.service.BookingEmailMessageService;
import com.poolcabs.model.Booking;
import com.poolcabs.model.BookingType;
import com.poolcabs.model.CabStatus;
import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class RegularBookingController implements Serializable {

    private Booking current;
    @EJB
    private com.poolcabs.dao.BookingFacade ejbFacade;
    @EJB
    private UserFacade userFacade;
    @EJB
    private TariffFacade tariffFacade;
    @EJB
    private BookingEmailMessageService bookingEmailMessageService;

    public RegularBookingController() {
    }

    @PostConstruct
    public void init() {
        getSelected().setBookingType(BookingType.REGULAR);
    }

    public Booking getSelected() {
        if (current == null) {
            current = new Booking();
        }
        return current;
    }

    private BookingFacade getFacade() {
        return ejbFacade;
    }

    public void create() {
        try {
            current.setStatus(CabStatus.PENDING);
            current.setCreateDate(new Date());
            current.setRoundTrip(true);

            getFacade().create(current);

        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }
}
