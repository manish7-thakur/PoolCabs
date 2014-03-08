/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.poll;

import com.poolcabs.dao.BookingFacade;
import com.poolcabs.distanceservice.GeocodeMaskService;
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
public class GeocodeMaskPoller {

    @EJB
    private BookingFacade facade;
    @EJB
    private GeocodeMaskService geocodeMaskService;

    public void poll() {
        List<Booking> bookingList = facade.findAllPastBookingsNotMasked();
        geocodeMaskService.maskGeocode(bookingList);
    }
}
