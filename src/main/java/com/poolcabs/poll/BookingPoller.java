/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.poll;

import com.poolcabs.bookingservice.BookingService;
import com.poolcabs.dao.BookingFacade;
import com.poolcabs.model.Booking;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author Manish
 */
@LocalBean
@Stateless
public class BookingPoller {

    @EJB
    private BookingFacade facade;
    @EJB
    private BookingService bookingService;

    public void poll() {
        List<Booking> bookingList = facade.findAllPending();
        bookingService.book(bookingList);
    }
}
