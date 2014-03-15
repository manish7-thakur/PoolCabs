/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.distanceservice;

import com.poolcabs.dao.BookingFacade;
import com.poolcabs.generated.directionservice.DirectionsResponse;
import com.poolcabs.generated.directionservice.DirectionsResponse.Route.Leg;
import com.poolcabs.model.Booking;
import com.poolcabs.util.JAXBHelper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.xml.sax.SAXException;

/**
 *
 * @author Manish
 */
@Stateless
@LocalBean
public class GeocodeService {

    @EJB
    private BookingFacade bookingFacade;

    public void geocode(List<Booking> bookingList) {
        for (Booking booking : bookingList) {
            URL url = null;
            try {
                url = getEncodedURL(booking.getPickupStreetAddress(), booking.getDropStreetAddress());
                DirectionsResponse response = JAXBHelper.<DirectionsResponse>unmarshall(url.openStream(), JAXBHelper.getSchemaFor(DirectionsResponse.class), "com.poolcabs.generated.directionservice");
                if (response.getStatus().equalsIgnoreCase("OK")) {
                    enrichBookingWithGeocodeInfo(booking, response);
                    bookingFacade.edit(booking);
                } else {
                    Logger.getLogger(GeocodeService.class.getName()).log(Level.SEVERE, response.getStatus());
                }
            } catch (MalformedURLException ex) {
                Logger.getLogger(GeocodeService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GeocodeService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(GeocodeService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(GeocodeService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private static String ENCODE_FORMAT = "UTF-8";

    private URL getEncodedURL(String pickupStreetAddress, String dropStreetAddress) throws MalformedURLException {
        String urlString = "";
        try {
            urlString = "https://maps.googleapis.com/maps/api/directions/xml?"
                    + "origin="
                    + URLEncoder.encode(pickupStreetAddress, ENCODE_FORMAT)
                    + "&destination="
                    + URLEncoder.encode(dropStreetAddress, ENCODE_FORMAT)
                    + "&sensor=false"
                    + "&key="
                    + URLEncoder.encode(ResourceBundle.getBundle("/Bundle").getString("Google_API_Key"), ENCODE_FORMAT)
                    + "";

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GeocodeService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new URL(urlString);
    }

    private void enrichBookingWithGeocodeInfo(Booking booking, DirectionsResponse response) {
        final Leg leg = response.getRoute().getLeg();
        booking.setDistanceInKM(leg.getDistance().getValue().doubleValue() / 1000);
        booking.getPickupGeoCode().setLatitude(leg.getStartLocation().getLat());
        booking.getPickupGeoCode().setLongitude(leg.getStartLocation().getLng());
        booking.getDropGeocode().setLatitude(leg.getEndLocation().getLat());
        booking.getDropGeocode().setLongitude(leg.getEndLocation().getLng());
        booking.setTotalCost(booking.getDistanceInKM() * booking.getTarrif());
    }

    public void startGeocoding() {
        List<Booking> bookingList = bookingFacade.findAllFutureBookingsWithMissingGeocodeInfo();
        geocode(bookingList);
    }
    
}
