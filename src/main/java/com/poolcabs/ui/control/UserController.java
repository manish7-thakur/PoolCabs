/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.ui.control;

import com.poolcabs.dao.UserFacade;
import com.poolcabs.dao.util.JsfUtil;
import com.poolcabs.messaging.service.UserRegistrationEmailMessageService;
import com.poolcabs.model.User;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Manish
 */
@ManagedBean
@ViewScoped
public class UserController {

    private static final Logger LOGGER = Logger.getLogger(UserController.class.getName());
    @EJB
    private UserFacade userfaceFacade;
    @EJB
    private UserRegistrationEmailMessageService mailService;
    private User newUserBeingCreated;
    private boolean registrationSuccessfull;
    private boolean registrationForm;

    @PostConstruct
    public void init() {
        newUserBeingCreated = new User();
        registrationSuccessfull = false;
        registrationForm = true;
    }

    public User getNewUserBeingCreated() {
        return newUserBeingCreated;
    }

    public void setNewUserBeingCreated(User newUserBeingCreated) {
        this.newUserBeingCreated = newUserBeingCreated;
    }

    public boolean isRegistrationSuccessfull() {
        return registrationSuccessfull;
    }

    public void setRegistrationSuccessfull(boolean registrationSuccessfull) {
        this.registrationSuccessfull = registrationSuccessfull;
    }

    public boolean isRegistrationForm() {
        return registrationForm;
    }

    public void setRegistrationForm(boolean registrationForm) {
        this.registrationForm = registrationForm;
    }

    public void save() {
        try {
            newUserBeingCreated.setCreatedDate(new Date());
            userfaceFacade.create(newUserBeingCreated);
            showSuccessfullRegistartionForm();
            mailService.sendMail(newUserBeingCreated);            
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("CreateUser_failed"));
        }
    }

    public void showSuccessfullRegistartionForm() {
        registrationSuccessfull = true;
        registrationForm = false;
    }

    public void passwordValidator(FacesContext context, UIComponent component, Object value) {
        if (!((String) value).equals(newUserBeingCreated.getPassword())) {
            throw new ValidatorException(new FacesMessage("Password doesn't match"));
        }
    }
}
