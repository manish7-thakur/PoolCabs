/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.poll;

import com.poolcabs.distanceservice.GeocodeService;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author Manish
 */
@LocalBean
@Stateless
public class GeocodePoller {

    @EJB
    private GeocodeService geocodeService;

    public void poll() {
        geocodeService.startGeocoding();
    }
}
