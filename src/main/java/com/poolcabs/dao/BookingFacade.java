/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.dao;

import com.poolcabs.model.Booking;
import com.poolcabs.model.CabStatus;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Manish
 */
@Stateless
public class BookingFacade extends AbstractFacade<Booking> {

    @PersistenceContext(unitName = "poolcabs")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BookingFacade() {
        super(Booking.class);
    }

    public List<Booking> findAllPending() {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Booking> bookingRoot = cq.from(Booking.class);
        cq.where(cb.equal(bookingRoot.get("status"), CabStatus.PENDING));
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public List<Booking> findAllByPhoneNumber(Long phoneNumber){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Booking> bookingRoot = cq.from(Booking.class);
        cq.where(cb.equal(bookingRoot.get("mobileNumber"), phoneNumber));
        return getEntityManager().createQuery(cq).getResultList();
    }
}
