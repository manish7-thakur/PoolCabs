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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 *
 * @author Manish
 */
@Entity
@Table(name = "SHARED_CAR_INFO")
public class SharedCarInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "MOBILE_NUMBER")
    private Long mobileNumber;
    @Column(name = "FROM_ADDRESS")
    private String fromAddress;
    @Column(name = "TO_ADDRESS")
    private String toAddress;
    @Column(name = "START_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    @Column(name = "RETURN_TIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date returnTime;
    @Column(name = "CLUBBED")
    private boolean clubbed;
    @Column(name = "CAR_OWNER")
    private boolean carOwner;
    @OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinColumn(name = "SHARED_WITH")
    private SharedCarInfo sharedWith;
    @Column(name = "ROUTE_DESCRIPTION")
    private String routeDescription;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_DATE")
    private Date createDate;
    @Version
    @Column(name = "MODIFIED_DATE")
    private Timestamp modifiedDate;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    public String getRouteDescription() {
        return routeDescription;
    }

    public void setRouteDescription(String routeDescription) {
        this.routeDescription = routeDescription;
    }

    public boolean isCarOwner() {
        return carOwner;
    }

    public void setCarOwner(boolean carOwner) {
        this.carOwner = carOwner;
    }

    public boolean isClubbed() {
        return clubbed;
    }

    public void setClubbed(boolean clubbed) {
        this.clubbed = clubbed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(Long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public SharedCarInfo getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(SharedCarInfo sharedWith) {
        this.sharedWith = sharedWith;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
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
        if (!(object instanceof SharedCarInfo)) {
            return false;
        }
        SharedCarInfo other = (SharedCarInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.poolcabs.model.CarOwner[ id=" + id + " ]";
    }
}
