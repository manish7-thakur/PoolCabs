/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.poolcabs.dao;

import java.util.Collection;
import java.util.List;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Manish
 */
public abstract class BaseDao<T> {
  @PersistenceContext(unitName = "poolcabs")
  protected EntityManager entityManager;

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public T save(T entity) {
    entityManager.persist(entity);
    return entity;
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void save(Collection<T> entities) {
    for(T entity: entities) {
      save(entity);
    }
  }
  
  /**
   * Synchronizes the provided entities with the currently persisted ones in the database:
   * - persists new entities,
   * - updates state of existing entities,
   * - removes entities from the database that are not provided in the argument.
   * @param entities Collection of entities to be synchronized.
   */
  @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
  public void synchronize(Collection<T> entities) {
  	for (T entity: entities) {
  		entityManager.merge(entity);
  	}
  	
  	for (T persistedEntity: getAll()) {
  		if (!entities.contains(persistedEntity)) {
  			entityManager.remove(persistedEntity);
  		}
  	}
  }
  
  public abstract List<T> getAll();
}
