/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.poll;

import com.poolcabs.bookingservice.BookingService;
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
    private BookingService bookingService;

    public void poll() {
        bookingService.startBooking();
    }
}
