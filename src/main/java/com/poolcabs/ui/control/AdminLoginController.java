/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.ui.control;

import com.poolcabs.dao.AdministratorFacade;
import com.poolcabs.dao.util.JsfUtil;
import com.poolcabs.model.Administrator;
import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Administrator
 */
@ManagedBean
@SessionScoped
public class AdminLoginController implements Serializable {

    private String name;
    private String password;
    private Administrator current;
    @EJB
    private AdministratorFacade administratorFacade;

    public String login() {
        current = administratorFacade.findByNameAndPassword(name, password);
        //System.out.println(current);
        if (current == null) {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("NoLoginFound"));
            return (name = password = null);
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("admin", current);
            return "/booking/List.jsf?faces-redirect=true";
        }
    }

    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "index?faces-redirect=true";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
