/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.poll;

import com.poolcabs.bookingservice.BookingService;
import com.poolcabs.dao.BookingFacade;
import com.poolcabs.distanceservice.GeocodeService;
import com.poolcabs.model.Booking;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author Manish
 */
@LocalBean
@Stateless
public class GeocodePoller {

    /**
     * Creates a new instance of GeocodePoller
     */
    @EJB
    private BookingFacade facade;
    @EJB
    private BookingService bookingService;
    @EJB
    private GeocodeService geocodeService;

    public void poll() {
        List<Booking> bookingList = facade.findAllFutureBookingsWithMissingGeocodeInfo();
        geocodeService.geocode(bookingList);
    }
}
