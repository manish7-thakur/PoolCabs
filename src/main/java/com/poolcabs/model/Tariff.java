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
    private Integer regularTariff;
    @Column(name = "INSTANT_TARIFF")
    private Integer instantTariff;
    @Column(name = "CASUAL_TARIFF")
    private Integer casualTariff;
    @Column(name = "OUTSTATION_TARIFF")
    private Integer outstationTariff;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRegularTariff() {
        return regularTariff;
    }

    public void setRegularTariff(Integer regularTariff) {
        this.regularTariff = regularTariff;
    }

    public Integer getInstantTariff() {
        return instantTariff;
    }

    public void setInstantTariff(Integer instantTariff) {
        this.instantTariff = instantTariff;
    }

    public Integer getCasualTariff() {
        return casualTariff;
    }

    public void setCasualTariff(Integer casualTariff) {
        this.casualTariff = casualTariff;
    }

    public Integer getOutstationTariff() {
        return outstationTariff;
    }

    public void setOutstationTariff(Integer outstationTariff) {
        this.outstationTariff = outstationTariff;
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
