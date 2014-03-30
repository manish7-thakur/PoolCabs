/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.ui.control;

import com.poolcabs.dao.UserFacade;
import com.poolcabs.dao.util.JsfUtil;
import com.poolcabs.model.User;
import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Manish
 */
@ManagedBean
@SessionScoped
public class UserLoginController implements Serializable {

    private Long phoneNumber;
    private String password;
    private User current;
    @EJB
    private UserFacade userFacade;

    public String login() {
        current = userFacade.findByPhoneNumberAndPassword(phoneNumber, password);
        //System.out.println(current);
        if (current == null) {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("NoLoginFound"));
            phoneNumber = null;
            return (password = null);
        } else if (null != current && !current.isActive()) {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("UserNotActive"));
            phoneNumber = null;
            return (password = null);
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("user", current);
            return "/booking/Create.jsf?faces-redirect=true";
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index?faces-redirect=true";
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
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
}
