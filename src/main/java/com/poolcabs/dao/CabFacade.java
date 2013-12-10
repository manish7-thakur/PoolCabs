/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.dao;

import com.poolcabs.model.Cab;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Manish
 */
@Stateless
public class CabFacade extends AbstractFacade<Cab> {

    @PersistenceContext(unitName = "poolcabs")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CabFacade() {
        super(Cab.class);
    }

    public List<Cab> findAllFreeCabs() {
        javax.persistence.criteria.CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<Cab> cabRoot = cq.from(Cab.class);
        cq.select(cabRoot);
        cq.where(cb.equal(cabRoot.get("booked"), 0));
        return getEntityManager().createQuery(cq).getResultList();
    }
}
