/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.ui.control;

import com.poolcabs.dto.BookingDTO;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Manish
 */
@ManagedBean
@ViewScoped
public class IndexController {

    private BookingDTO bookingDTO;
 
    @PostConstruct
    public void init(){
        bookingDTO = new BookingDTO();
    }

    public BookingDTO getBookingDTO() {
        return bookingDTO;
    }

    public void setBookingDTO(BookingDTO bookingDTO) {
        this.bookingDTO = bookingDTO;
    }
    
    public String setCurrentBooking(String type){
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("bookingType", type);
        return "booking/Create.jsf?faces-redirect=true";
    }
   
}
