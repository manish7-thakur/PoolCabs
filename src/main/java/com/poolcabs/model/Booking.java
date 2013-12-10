/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.model;

import java.io.Serializable;
import java.sql.Time;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 *
 * @author Manish
 */
@Entity
@Table(name = "REGULAR_BOOKINGS_INFO")
public class Booking implements Serializable, Cloneable {

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
    private Long mobileNumber;
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
    @Temporal(TemporalType.TIMESTAMP)
    private Date pickupTime;
    @Column(name = "DROP_STREET_ADDRESS")
    private String dropStreetAddress;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "DROP_GEOCODE")
    private GeoCode dropGeocode;
    @Column(name = "DROP_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date returnPickUpTime;
    @Column(name = "ROUND_TRIP")
    private boolean roundTrip;
    @Column(name = "DISTANCE_IN_KM")
    private Double distanceInKM;
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "CAB")
    private Cab cab;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createDate;
    @Version
    @Column(name = "MODIFIED_DATE")
    private Timestamp modifiedDate;

    public Booking() {
        dropGeocode = new GeoCode();
        pickupGeoCode = new GeoCode();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BookingType getBookingType() {
        return bookingType;
    }

    public void setBookingType(BookingType bookingType) {
        this.bookingType = bookingType;
    }

    public CabStatus getStatus() {
        return status;
    }

    public void setStatus(CabStatus status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Date getRideStartDate() {
        return rideStartDate;
    }

    public void setRideStartDate(Date rideStartDate) {
        this.rideStartDate = rideStartDate;
    }

    public Date getRideEndDate() {
        return rideEndDate;
    }

    public void setRideEndDate(Date rideEndDate) {
        this.rideEndDate = rideEndDate;
    }

    public String getPickupStreetAddress() {
        return pickupStreetAddress;
    }

    public void setPickupStreetAddress(String pickupStreetAddress) {
        this.pickupStreetAddress = pickupStreetAddress;
    }

    public Date getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Date pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getDropStreetAddress() {
        return dropStreetAddress;
    }

    public void setDropStreetAddress(String dropStreetAddress) {
        this.dropStreetAddress = dropStreetAddress;
    }

    public GeoCode getPickupGeoCode() {
        return pickupGeoCode;
    }

    public void setPickupGeoCode(GeoCode pickupGeoCode) {
        this.pickupGeoCode = pickupGeoCode;
    }

    public GeoCode getDropGeocode() {
        return dropGeocode;
    }

    public void setDropGeocode(GeoCode dropGeocode) {
        this.dropGeocode = dropGeocode;
    }

    public Date getReturnPickUpTime() {
        return returnPickUpTime;
    }

    public void setReturnPickUpTime(Date dropTime) {
        this.returnPickUpTime = dropTime;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public boolean isRoundTrip() {
        return roundTrip;
    }

    public void setRoundTrip(boolean roundTrip) {
        this.roundTrip = roundTrip;
    }

    public Double getDistanceInKM() {
        return distanceInKM;
    }

    public Cab getCab() {
        return cab;
    }

    public void setCab(Cab cab) {
        this.cab = cab;
    }

    public void setDistanceInKM(Double distanceInKM) {
        this.distanceInKM = distanceInKM;
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
        if (!(object instanceof Booking)) {
            return false;
        }
        Booking other = (Booking) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public Booking clone() throws CloneNotSupportedException {
        return (Booking) super.clone();
    }

    @Override
    public String toString() {
        return "com.poolcabs.model.Booking[ id=" + id + " ]";
    }
}
