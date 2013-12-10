/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.ui.control;

import com.poolcabs.dao.UserFacade;
import com.poolcabs.messaging.service.UserRegistrationEmailMessageService;
import com.poolcabs.model.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
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

        Set<String> addressList = new HashSet<String>();
        addressList.add("Add1");
        addressList.add("Add2");
        newUserBeingCreated.setAddressSet(addressList);
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
        // List<User> list = userfaceFacade.findAll();
        newUserBeingCreated.setCreatedDate(new Date());
        userfaceFacade.create(newUserBeingCreated);
        mailService.sendMail(newUserBeingCreated);
    }

    public void passwordValidator(FacesContext context, UIComponent component, Object value) {
        if (!((String) value).equals(newUserBeingCreated.getPassword())) {
            throw new ValidatorException(new FacesMessage("Password doesn't match"));
        }
    }
}
