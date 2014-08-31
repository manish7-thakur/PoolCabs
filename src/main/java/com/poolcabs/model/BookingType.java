/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author Manish
 */
@ManagedBean
public enum BookingType {
    REGULAR("Regular"),
    CASUAL("Casual"),
    INSTANT("Instant"),
    PERSONAL("Personal"),
    OUTSTATION("OutStation");
    
    private String value;
    
    private BookingType(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static List<BookingType> list() {
        return Collections.unmodifiableList(Arrays.asList(BookingType.values()));
    }
}
