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

    @PostConstruct
    public void init() {
        newUserBeingCreated = new User();

        //Set<String> addressList = new HashSet<String>();
        //addressList.add("Add1");
        //addressList.add("Add2");
        //newUserBeingCreated.setAddressSet(addressList);
//        newUserBeingCreated.setBirthDate(new Date());
//        newUserBeingCreated.setMobileNumber(583930);
//        newUserBeingCreated.setSex('F');
//        newUserBeingCreated.setStreetAddress("hgf");
//        newUserBeingCreated.setCity("sdfsd");
//        newUserBeingCreated.setState("state");
//        newUserBeingCreated.setZipcode("sdfs");
//        newUserBeingCreated.setLandmark("sfds");
//        newUserBeingCreated.setDropStreetAdress("3eew");
//        newUserBeingCreated.setDropCity("sdfs");
//        newUserBeingCreated.setDropState("sdfsdf");
//        newUserBeingCreated.setDropZipCode("asdas");
//        newUserBeingCreated.setDropLandmark("asdsd");
//        newUserBeingCreated.setCreatedDate(new Date());
//        newUserBeingCreated.setModifiedDate(new Timestamp(325235));
    }

    public User getNewUserBeingCreated() {
        return newUserBeingCreated;
    }

    public void setNewUserBeingCreated(User newUserBeingCreated) {
        this.newUserBeingCreated = newUserBeingCreated;
    }

    public void save() {

        newUserBeingCreated.setCreatedDate(new Date());
        userfaceFacade.create(newUserBeingCreated);
        try {
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("CreateUser_Registration"));
            mailService.sendMail(newUserBeingCreated);
            final ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.redirect(externalContext.getRequestContextPath() + "/booking/userbookings.jsf");
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("CreateUser_failed"));
        }
    }

    public void passwordValidator(FacesContext context, UIComponent component, Object value) {
        if (!((String) value).equals(newUserBeingCreated.getPassword())) {
            throw new ValidatorException(new FacesMessage("Password doesn't match"));
        }
    }
}
