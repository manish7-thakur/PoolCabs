/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.dao;

import com.poolcabs.model.Booking;
import com.poolcabs.model.BookingType;
import com.poolcabs.model.CabStatus;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
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

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Booking> findAllPendingForNextHour(BookingType booking) {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Booking> bookingRoot = cq.from(Booking.class);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 2);
        Predicate greater = cb.and(cb.greaterThanOrEqualTo(bookingRoot.<Date>get("pickupTime"), calendar.getTime()));
        calendar.add(Calendar.HOUR, 1);
        Predicate lowerAndStatus = cb.and(cb.equal(bookingRoot.get("status"), CabStatus.PENDING), cb.lessThanOrEqualTo(bookingRoot.<Date>get("pickupTime"), calendar.getTime()));
        Predicate bookingType = cb.and(cb.equal(bookingRoot.get("bookingType"), booking));
        cq.where(cb.and(greater, lowerAndStatus, bookingType));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<Booking> findAllByPhoneNumber(Long phoneNumber) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Booking> bookingRoot = cq.from(Booking.class);
        cq.where(cb.equal(bookingRoot.get("mobileNumber"), phoneNumber));
        return getEntityManager().createQuery(cq).getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Booking> findAllFutureBookingsWithMissingGeocodeInfo() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Booking> bookingRoot = cq.from(Booking.class);
        cq.where(cb.greaterThanOrEqualTo(bookingRoot.<Date>get("pickupTime"), new Date()), cb.isNull(bookingRoot.get("distanceInKM")));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<Booking> findAllPastBookingsNotMasked() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Booking> bookingRoot = cq.from(Booking.class);
        cq.where(cb.lessThan(bookingRoot.<Date>get("pickupTime"), new Date()), cb.isNotNull(bookingRoot.get("pickupGeoCode").get("latitude")));
        return getEntityManager().createQuery(cq).getResultList();
    }
}
