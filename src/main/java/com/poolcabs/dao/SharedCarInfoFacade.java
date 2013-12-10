/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.dao;

import com.poolcabs.model.SharedCarInfo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Manish
 */
@Stateless
public class SharedCarInfoFacade extends AbstractFacade<SharedCarInfo> {
    @PersistenceContext(unitName = "poolcabs")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SharedCarInfoFacade() {
        super(SharedCarInfo.class);
    }
    
}
