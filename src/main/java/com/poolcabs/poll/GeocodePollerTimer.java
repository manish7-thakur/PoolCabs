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
public class GeocodePollerTimer {

    @EJB
    private GeocodePoller poller;
    
    @Schedule(minute = "*", hour = "*", persistent = false)
    public void pollTimer() {
        Logger.getLogger(GeocodePollerTimer.class.getName()).log(Level.INFO, "Starting GeocodePoller at : {0}", new Date());
        startGeocodePoller();
        Logger.getLogger(GeocodePollerTimer.class.getName()).log(Level.INFO, "GeocodePoller finished at : {0}", new Date());
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    private void startGeocodePoller() {
        poller.poll();
    }
}
