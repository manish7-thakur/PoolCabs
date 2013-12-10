/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.ui.control;

import com.poolcabs.dao.UserFacade;
import com.poolcabs.dao.util.JsfUtil;
import com.poolcabs.model.BookingType;
import com.poolcabs.model.User;
import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Manish
 */
@ManagedBean
@SessionScoped
public class UserLoginController implements Serializable{

    private String email;
    private String password;
    private User current;
    private BookingType currentBookingType;
    @EJB
    private UserFacade userFacade;
    
    public String login() {
        current = userFacade.findByEmailAndPassword(email, password);
        //System.out.println(current);
        if (current == null) {            
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("NoLoginFound"));
            return (email = password = null);
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", current);
            return "/booking/Create.jsf?faces-redirect=true";
        }
    }
    
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index?faces-redirect=true";
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isLoggedIn() {
        return current != null;
    }

    public BookingType getCurrentBookingType() {
        return currentBookingType;
    }

    public void setCurrentBookingType(BookingType currentBookingType) {
        this.currentBookingType = currentBookingType;
    }    
}
