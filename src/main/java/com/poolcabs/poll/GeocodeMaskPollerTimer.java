/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.poll;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

/**
 *
 * @author Manish
 */
@Singleton
public class GeocodeMaskPollerTimer {

    @EJB
    private GeocodeMaskPoller geocodeMaskPoller;

    @Schedule(minute = "0", hour = "0", persistent = false)
    public void pollTimer() {
        Logger.getLogger(GeocodeMaskPollerTimer.class.getName()).log(Level.INFO, "Starting GeocodeMaskPoller at : {0}", new Date());
        startGeocodeMaskPoller();
        Logger.getLogger(GeocodeMaskPollerTimer.class.getName()).log(Level.INFO, "GeocodeMaskPoller finished at : {0}", new Date());
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    private void startGeocodeMaskPoller() {
        geocodeMaskPoller.poll();
    }
}
