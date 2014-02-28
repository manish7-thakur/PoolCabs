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
public class BookingPollerTimer {

    @EJB
    private BookingPoller poller;
    
    @Schedule(minute = "*/5", hour = "*", persistent = false)
    public void pollTimer() {
        Logger.getLogger(BookingPollerTimer.class.getName()).log(Level.INFO, "Starting BookingPoller at : {0}", new Date());
        startBookingPoller();
        Logger.getLogger(BookingPollerTimer.class.getName()).log(Level.INFO, "BookingPoller finished at : {0}", new Date());
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    private void startBookingPoller() {
        poller.poll();
    }
}
