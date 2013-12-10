/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.distanceservice;

import com.poolcabs.model.GeoCode;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author Manish
 */
@Stateless
@LocalBean
public class HaversineDistanceCalculator {
    
    public double getStraightLineDistance(GeoCode code1, GeoCode code2){
        double $lat1 = code1.getLatitude();
        double $lon1 = code1.getLongitude();
        double $lat2 = code2.getLatitude();
        double $lon2 = code2.getLongitude();
        double $latd = Math.toRadians($lat2 - $lat1);
        double $lond = Math.toRadians($lon2 - $lon1);
        double $a = Math.sin($latd / 2) * Math.sin($latd / 2)
                + Math.cos(Math.toRadians($lat1)) * Math.cos(Math.toRadians($lat2))
                * Math.sin($lond / 2) * Math.sin($lond / 2);
        double $c = 2 * Math.atan2(Math.sqrt($a), Math.sqrt(1 - $a));
        return (6371.0 * $c);        
    }
    
        public double getStraightLineDistance(double $lat1, double $lon1, double $lat2, double $lon2){
        double $latd = Math.toRadians($lat2 - $lat1);
        double $lond = Math.toRadians($lon2 - $lon1);
        double $a = Math.sin($latd / 2) * Math.sin($latd / 2)
                + Math.cos(Math.toRadians($lat1)) * Math.cos(Math.toRadians($lat2))
                * Math.sin($lond / 2) * Math.sin($lond / 2);
        double $c = 2 * Math.atan2(Math.sqrt($a), Math.sqrt(1 - $a));
        return (6371.0 * $c);        
    }
}
