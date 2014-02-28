/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Manish
 */
@Entity
@Table(name = "SETTINGS_INFO")
public class Settings implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "PERMISSIBLE_DISTANCE_FOR_PICKUP")
    private double permissibleDistanceForPickup;
    @Column(name = "PERMISSIBLE_DISTANCE_FOR_DROP")
    private double permissibleDistanceForDrop;
    @Column(name = "PERMISSIBLE_TIME_WINDOW")
    private int permissibleTimeWindow;
    @Column(name = "BOOKING_CLUB_COUNT")
    private int clubBookingCount;
    @Column(name = "VENDOR_EMAIL")
    private String vendorEmail;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getPermissibleDistanceForPickup() {
        return permissibleDistanceForPickup;
    }

    public void setPermissibleDistanceForPickup(double permissibleDistanceForPickup) {
        this.permissibleDistanceForPickup = permissibleDistanceForPickup;
    }

    public double getPermissibleDistanceForDrop() {
        return permissibleDistanceForDrop;
    }

    public void setPermissibleDistanceForDrop(double permissibleDistanceForDrop) {
        this.permissibleDistanceForDrop = permissibleDistanceForDrop;
    }

    public int getPermissibleTimeWindow() {
        return permissibleTimeWindow;
    }

    public void setPermissibleTimeWindow(int permissibleTimeWindow) {
        this.permissibleTimeWindow = permissibleTimeWindow;
    }

    public int getClubBookingCount() {
        return clubBookingCount;
    }

    public void setClubBookingCount(int clubBookingCount) {
        this.clubBookingCount = clubBookingCount;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Settings)) {
            return false;
        }
        Settings other = (Settings) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.poolcabs.model.Settings[ id=" + id + " ]";
    }
}
