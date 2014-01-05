/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.dao;

import com.poolcabs.model.Administrator;
import com.poolcabs.model.User;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

/**
 *
 * @author Administrator
 */
@Stateless
public class AdministratorFacade extends AbstractFacade<Administrator> {

    @PersistenceContext(unitName = "poolcabs")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AdministratorFacade() {
        super(Administrator.class);
    }

    public Administrator findByNameAndPassword(String name, String password) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        javax.persistence.criteria.CriteriaQuery cq = cb.createQuery(Administrator.class);
        Root<User> adminRoot = cq.from(Administrator.class);
        cq.where(cb.and(cb.equal(adminRoot.get("name"), name), cb.equal(adminRoot.get("password"), password)));
        Query q = em.createQuery(cq);
        List<Object> objectList = q.getResultList();
        if (objectList.isEmpty()) {
            return null;
        } else {
            return (Administrator) objectList.get(0);
        }
    }
}
