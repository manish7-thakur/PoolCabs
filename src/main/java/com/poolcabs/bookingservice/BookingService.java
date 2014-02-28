/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.bookingservice;

import com.poolcabs.dao.BookingFacade;
import com.poolcabs.dao.CabFacade;
import com.poolcabs.dao.SettingsFacade;
import com.poolcabs.distanceservice.HaversineDistanceCalculator;
import com.poolcabs.messaging.MessagingRunnble;
import com.poolcabs.messaging.service.BookingMessagingService;
import com.poolcabs.model.Booking;
import com.poolcabs.model.Cab;
import com.poolcabs.model.CabStatus;
import com.poolcabs.model.Settings;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Manish
 */
@Stateless
public class BookingService {

    @EJB
    private HaversineDistanceCalculator distanceCalculator;
    @EJB
    private BookingFacade bookingFacade;
    @EJB
    private CabFacade cabFacade;
    @EJB
    private BookingMessagingService bookingMessagingService;
    @EJB
    private SettingsFacade settingsFacade;
    
    private static double MAXIMUM_PERMISSIBLE_DISTANCE_IN_KM_PICKUP;
    private static double MAXIMUM_PERMISSIBLE_DISTANCE_IN_KM_DROP;
    private static long MAXIMUM_PERMISSIBLE_TIME_WINDOW_MINUTES;
    private static int CAB_SIZE;
    private List<Booking> clubbedBookings = new ArrayList<Booking>();

    public void book(List<Booking> bookingList) {
        initializeProperties();
        for (int i = 0; i < bookingList.size(); i++) {
            Booking booking = bookingList.get(i);
            clubbedBookings.add(bookingList.get(i));
            for (int j = i + 1; j < bookingList.size(); j++) {
                double pickUpDistance = distanceCalculator.getStraightLineDistance(bookingList.get(j).getPickupGeoCode(), booking.getPickupGeoCode());
                double dropDistance = distanceCalculator.getStraightLineDistance(bookingList.get(j).getDropGeocode(), booking.getDropGeocode());
                if (pickUpDistance <= MAXIMUM_PERMISSIBLE_DISTANCE_IN_KM_PICKUP && dropDistance <= MAXIMUM_PERMISSIBLE_DISTANCE_IN_KM_DROP && pickUpTimeInPermissibleWindow(bookingList.get(j).getPickupTime(), booking.getPickupTime())) {
                    clubbedBookings.add(bookingList.get(j));
                    if (clubbedBookings.size() == CAB_SIZE) {
                        allocateCab(clubbedBookings);
                        informAllThroughMessage(clubbedBookings);
                        bookingList.removeAll(clubbedBookings);
                        --i;
                    }
                }
            }
            clubbedBookings.clear();
        }

    }

    public void allocateCab(List<Booking> clubbedBookings) {
        Cab cab = getFreeCab();
        cab.setBooked(true);
        for (Booking booking : clubbedBookings) {
            booking.setStatus(CabStatus.BOOKED);
            booking.setCab(cab);
            bookingFacade.edit(booking);
        }       
    }
 
    private Cab getFreeCab() {
        return cabFacade.findAllFreeCabs().get(0);
    }

    public void informAllThroughMessage(List<Booking> clubbedBookings) {
        List<URL> messageURLList = bookingMessagingService.createMessagingURLList(clubbedBookings);
        new Thread(new MessagingRunnble(messageURLList)).start();
    }

    private boolean pickUpTimeInPermissibleWindow(Date date1, Date date2) {
        long diff = date1.getTime() - date2.getTime();
        long diffInMin = Math.abs(diff / (1000 * 60));
        if (diffInMin <= MAXIMUM_PERMISSIBLE_TIME_WINDOW_MINUTES) {
            return true;
        } else {
            return false;
        }

    }

    private void initializeProperties() {
        Settings settings;
        settings = settingsFacade.findAll().get(0);
        MAXIMUM_PERMISSIBLE_DISTANCE_IN_KM_PICKUP = settings.getPermissibleDistanceForPickup();
        MAXIMUM_PERMISSIBLE_DISTANCE_IN_KM_DROP = settings.getPermissibleDistanceForDrop();
        MAXIMUM_PERMISSIBLE_TIME_WINDOW_MINUTES = settings.getPermissibleTimeWindow();
        CAB_SIZE = settings.getClubBookingCount();
    }
}
