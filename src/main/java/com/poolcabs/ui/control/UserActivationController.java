package com.poolcabs.ui.control;

import com.poolcabs.dao.UserFacade;
import com.poolcabs.dao.util.JsfUtil;
import com.poolcabs.model.User;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Manish
 */
@ManagedBean
@RequestScoped
public class UserActivationController implements Serializable {

    @ManagedProperty(value = "#{param.key}")
    private Long key;
    @EJB
    private UserFacade userFacade;

    private boolean valid = true;

    @PostConstruct
    public void init() {
        User user = userFacade.find(key);
        if (null != user && !user.isActive()) {
            user.setActive(true);
            try {
                userFacade.edit(user);
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("UserActivated"));
            } catch (Exception e) {
                Logger.getLogger(UserActivationController.class.getName()).log(Level.SEVERE, null, e);
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("UserActivationError"));
            }

        } else {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("UserActivationInvalidRequest"));
        }
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }
}
