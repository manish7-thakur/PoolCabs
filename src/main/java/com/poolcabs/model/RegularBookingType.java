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
 * @author MTHAKUR
 */
@ManagedBean
public enum RegularBookingType {

    BUDGET("Budget"),
    PREMIUM("Premium");
    private String value;

    private RegularBookingType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static List<RegularBookingType> list() {
        return Collections.unmodifiableList(Arrays.asList(RegularBookingType.values()));
    }
}
