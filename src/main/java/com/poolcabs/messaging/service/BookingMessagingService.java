/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.messaging.service;

import com.poolcabs.model.Booking;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author Manish
 */
@LocalBean
@Stateless
public class BookingMessagingService {

    public List<URL> createMessagingURLList(List<Booking> bookedList) {
        List<URL> messagingURL = null;
        URL url = null;
        try {
            messagingURL = new ArrayList<URL>();            
            for (Booking booking : bookedList) {
                url = encodeBookingMessageURL(booking);
                messagingURL.add(url);
            }
            url = encodeDriverMesaageURL(bookedList);
            messagingURL.add(url);
        } catch (MalformedURLException ex) {
            Logger.getLogger(BookingMessagingService.class.getName()).log(Level.SEVERE, url.toString(), ex);
        }
        return messagingURL;

    }

    private URL encodeBookingMessageURL(Booking booking) throws MalformedURLException {
        String urlString = "";
        try {
            urlString = "http://api.mvaayoo.com/mvaayooapi/MessageCompose?user=thakur7.manish@yahoo.in:5zn2fptfmb&senderID=";
            urlString += URLEncoder.encode("TEST SMS", "UTF-8")
                    + "&receipientno="
                    + URLEncoder.encode(String.valueOf(booking.getMobileNumber()), "UTF-8")
                    + "&dcs=0"
                    + "&msgtxt="
                    + URLEncoder.encode("Your cab has been booked. The cab no. is " + booking.getCab().getCabNo()
                    + "and driver name is " + booking.getCab().getDriverName()
                    + "We wish you happy and safe journey. Regards poolcabs.", "UTF-8")
                    + "&state=4";

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BookingMessagingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new URL(urlString);
    }

    private URL encodeDriverMesaageURL(List<Booking> bookedList) throws MalformedURLException {

        StringBuilder addressStringBuilder = new StringBuilder();
        for (int i = 0; i < bookedList.size(); i++) {
            addressStringBuilder.append(String.valueOf(i) + "Address :" + bookedList.get(i).getPickupStreetAddress() + "\n");
            addressStringBuilder.append("Phone No. : " + bookedList.get(i).getMobileNumber());
        }
        String urlString = "";
        try {
            urlString = "http://api.mvaayoo.com/mvaayooapi/MessageCompose?user=thakur7.manish@yahoo.in:5zn2fptfmb&senderID=";
            urlString += URLEncoder.encode("TEST SMS", "UTF-8")
                    + "&receipientno="
                    + URLEncoder.encode(String.valueOf(bookedList.get(0).getCab().getDriverPhoneNumber()), "UTF-8")
                    + "&dcs=0"
                    + "&msgtxt="
                    + URLEncoder.encode("Hi " + bookedList.get(0).getCab().getDriverName() + ". "
                    + "Please pickup the following persons from the mentioned address : " + addressStringBuilder.toString(), "UTF-8")
                    + "&state=4";


        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(BookingMessagingService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new URL(urlString);
    }
}
