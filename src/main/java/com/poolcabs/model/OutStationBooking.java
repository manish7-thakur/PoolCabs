/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 *
 * @author Manish
 */
@Entity
public class OutStationBooking implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "BOOKING_TYPE")
    @Enumerated(EnumType.STRING)
    private BookingType bookingType;
    @Column(name = "CAB_STATUS")
    @Enumerated(EnumType.STRING)
    private CabStatus status;
    @Column(name = "CUSTOMER_NAME")
    private String customerName;
    @Column(name = "MOBILE_NUMBER")
    private long mobileNumber;
    @Temporal(TemporalType.DATE)
    @Column(name = "RIDE_START_DATE")
    private Date rideStartDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "RIDE_END_DATE")
    private Date rideEndDate;
    @Column(name = "PICKUP_STREET_ADDRESS")
    private String pickupStreetAddress;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "PICKUP_GEOCODE")
    private GeoCode pickupGeoCode;
    @Column(name = "PICKUP_TIME")
    private String pickupTime;
    @Column(name = "DROP_STREET_ADDRESS")
    private String dropStreetAddress;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "DROP_GEOCODE")
    private GeoCode dropGeocode;
    @Column(name = "DROP_TIME")
    private String dropTime;
    @Column(name = "ROUND_TRIP")
    private boolean roundTrip;
    @Column(name = "DISTANCE_IN_KM")
    private Double distanceInKM;
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "CAB")
    private Cab cab;
    @Temporal(TemporalType.DATE)
    @Column(name = "CREATED_DATE")
    private Date createDate;
    @Version
    @Column(name = "MODIFIED_DATE")
    private Timestamp modifiedDate;

    public OutStationBooking() {
        dropGeocode = new GeoCode();
        pickupGeoCode = new GeoCode();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof OutStationBooking)) {
            return false;
        }
        OutStationBooking other = (OutStationBooking) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.poolcabs.model.OutStationBooking[ id=" + id + " ]";
    }
    
}
