/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.poll;

import com.poolcabs.distanceservice.GeocodeService;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Stateless;

/**
 *
 * @author Manish
 */
@Singleton
public class GeocodePoller {

    @EJB
    private GeocodeService geocodeService;

    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void poll() {
        Logger.getLogger(GeocodePoller.class.getName()).log(Level.INFO, "Starting GeocodePoller at : {0}", new Date());
        geocodeService.startGeocoding();
        Logger.getLogger(GeocodePoller.class.getName()).log(Level.INFO, "GeocodePoller finished at : {0}", new Date());
    }
}
