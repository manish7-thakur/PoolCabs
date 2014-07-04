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
@Table(name = "TARIFF_INFO")
public class Tariff implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "REGULAR_TARIFF")
    private Double regularTariff;
    @Column(name = "INSTANT_TARIFF")
    private Double instantTariff;
    @Column(name = "CASUAL_TARIFF")
    private Double casualTariff;
    @Column(name = "PERSONAL_TARIFF")
    private Double personalTariff;
    @Column(name = "WOMEN_TARIFF")
    private Double womenTariff;
    @Column(name = "OUTSTATION_TARIFF")
    private Double outstationTariff;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRegularTariff() {
        return regularTariff;
    }

    public void setRegularTariff(Double regularTariff) {
        this.regularTariff = regularTariff;
    }

    public Double getInstantTariff() {
        return instantTariff;
    }

    public void setInstantTariff(Double instantTariff) {
        this.instantTariff = instantTariff;
    }

    public Double getCasualTariff() {
        return casualTariff;
    }

    public void setCasualTariff(Double casualTariff) {
        this.casualTariff = casualTariff;
    }

    public Double getOutstationTariff() {
        return outstationTariff;
    }

    public void setOutstationTariff(Double outstationTariff) {
        this.outstationTariff = outstationTariff;
    }

    public Double getPersonalTariff() {
        return personalTariff;
    }

    public void setPersonalTariff(Double personalTariff) {
        this.personalTariff = personalTariff;
    }

    public Double getWomenTariff() {
        return womenTariff;
    }

    public void setWomenTariff(Double womenTariff) {
        this.womenTariff = womenTariff;
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
        if (!(object instanceof Tariff)) {
            return false;
        }
        Tariff other = (Tariff) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.poolcabs.model.Tarrif[ id=" + id + " ]";
    }
}
