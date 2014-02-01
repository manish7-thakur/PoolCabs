/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.dto;

import com.poolcabs.model.User;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 *
 * @author Administrator
 */
@ManagedBean
@SessionScoped
public class SessionGlobals implements Serializable{

    private User currentUser;

    /**
     * Creates a new instance of SessionGlobals
     */
    public SessionGlobals() {
    }

    @PostConstruct
    public void init() {
        currentUser = (User) getSessionMap().get("user");
    }

    public String getUserName() {
        if (null != currentUser) {
           return currentUser.getName();
        }
        return null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private Map<String, Object> getSessionMap() {
        return getExternalContext().getSessionMap();
    }

    private ExternalContext getExternalContext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }
}
