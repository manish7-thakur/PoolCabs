/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Manish
 */
public enum CabStatus {
    PENDING("Pending"),
    BOOKED("Booked"),
    CANCELLED("Cancelled"),
    DONE("Done");    
     
    private CabStatus(String value){
        this.value = value;
    }
    
    public static List<CabStatus> list(){
        return Collections.unmodifiableList(Arrays.asList(CabStatus.values()));        
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
      private String value;
}
