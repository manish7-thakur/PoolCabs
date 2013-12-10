/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.ui.control;

import java.io.Serializable;
import javax.annotation.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Manish
 */
@ManagedBean
@SessionScoped
public class MenuBean implements Serializable{

    public String getCoolTab() {
        return coolTab;
    }

    public String getComfortableTab() {
        return comfortableTab;
    }

    public String getReliableTab() {
        return reliableTab;
    }

    public String getTwentyFourSevenTab() {
        return TwentyFourSevenTab;
    }
    
    private String coolTab;
    private String comfortableTab;
    private String reliableTab;
    private String TwentyFourSevenTab;
    
}
