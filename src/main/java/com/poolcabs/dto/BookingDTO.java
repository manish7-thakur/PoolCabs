/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.dto;

import com.poolcabs.model.BookingType;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Manish
 */
@ManagedBean
@SessionScoped
public class BookingDTO {

    /**
     * Creates a new instance of BookingDTO
     */
    public BookingDTO() {
    }
    private BookingType bookingType;

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }
}
