/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.distanceservice;

import com.poolcabs.dao.BookingFacade;
import com.poolcabs.dao.GeoCodeFacade;
import com.poolcabs.model.Booking;
import com.poolcabs.poll.GeocodePoller;
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
@Stateless
@LocalBean
public class GeocodeMaskService {

    @EJB
    private BookingFacade bookingFacade;
    @EJB
    private GeoCodeFacade geocodeFacade;

    public void maskGeocode(List<Booking> bookingList) {
        for (Booking booking : bookingList) {
            try {
                deleteAndMaskGeocodeInfo(booking);
                bookingFacade.edit(booking);
            } catch (Exception ex) {
                Logger.getLogger(GeocodePoller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void deleteAndMaskGeocodeInfo(Booking booking) {
        booking.getPickupGeoCode().setLatitude(null);
        booking.getPickupGeoCode().setLongitude(null);
        booking.getDropGeocode().setLatitude(null);
        booking.getDropGeocode().setLongitude(null);
    }
}
