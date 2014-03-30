/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.dao;

import com.poolcabs.model.User;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

/**
 *
 * @author Manish
 */
@LocalBean
@Stateless
public class UserFacade extends AbstractFacade<User> {

    @PersistenceContext(unitName = "poolcabs")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UserFacade() {
        super(User.class);
    }

    public User findByEmailAndPassword(String email, String password) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery(User.class);
        Root<User> userRoot = cq.from(User.class);
        cq.where(cb.and(cb.equal(userRoot.get("email"), email), cb.equal(userRoot.get("password"), password)));
        Query q = em.createQuery(cq);
        List<Object> objectList = q.getResultList();
        if (objectList.isEmpty()) {
            return null;
        } else {
            return (User) objectList.get(0);
        }
    }

    public User findByPhoneNumberAndPassword(Long phoneNumber, String password) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery(User.class);
        Root<User> userRoot = cq.from(User.class);
        cq.where(cb.and(cb.equal(userRoot.get("mobileNumber"), phoneNumber), cb.equal(userRoot.get("password"), password)));
        Query q = em.createQuery(cq);
        List<Object> objectList = q.getResultList();
        if (objectList.isEmpty()) {
            return null;
        } else {
            return (User) objectList.get(0);
        }
    }

    public User findByEmail(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery(User.class);
        Root<User> userRoot = cq.from(User.class);
        cq.where(cb.equal(userRoot.get("email"), email));
        Query q = em.createQuery(cq);
        List<Object> objectList = q.getResultList();
        if (objectList.isEmpty()) {
            return null;
        } else {
            return (User) objectList.get(0);
        }
    }

    public User findByPhoneNumber(Long phoneNumber) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery(User.class);
        Root<User> userRoot = cq.from(User.class);
        cq.where(cb.equal(userRoot.get("mobileNumber"), phoneNumber));
        Query q = em.createQuery(cq);
        List<Object> objectList = q.getResultList();
        if (objectList.isEmpty()) {
            return null;
        } else {
            return (User) objectList.get(0);
        }
    }

    public User findByActivationKey(String key) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery(User.class);
        Root<User> userRoot = cq.from(User.class);
        cq.where(cb.equal(userRoot.get("activationKey"), key));
        Query q = em.createQuery(cq);
        List<Object> objectList = q.getResultList();
        if (objectList.isEmpty()) {
            return null;
        } else {
            return (User) objectList.get(0);
        }
    }
}
