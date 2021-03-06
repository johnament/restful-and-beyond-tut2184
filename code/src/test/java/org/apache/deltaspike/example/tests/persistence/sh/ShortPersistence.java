/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 *     distributed with this work for additional information
 *     regarding copyright ownership.  The ASF licenses this file
 *     to you under the Apache License, Version 2.0 (the
 *     "License"); you may not use this file except in compliance
 *     with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an
 *     "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *     KIND, either express or implied.  See the License for the
 *     specific language governing permissions and limitations
 *     under the License.
 */

package org.apache.deltaspike.example.tests.persistence.sh;

import org.apache.deltaspike.jpa.api.entitymanager.PersistenceUnitName;
import org.apache.log4j.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

@ApplicationScoped
@Vetoed
public class ShortPersistence {
    @Inject
    @PersistenceUnitName("DefaultApp")
    private EntityManagerFactory entityManagerFactory;

    private final Logger logger = Logger.getLogger(ShortPersistence.class);

    @Produces
    @RequestScoped
    public EntityManager entityManager() {
        logger.info("Creating an entity manager.");
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction et = em.getTransaction();
        et.begin();
        return em;
    }

    public void cleanEM(@Disposes EntityManager entityManager) {
        logger.info("Disposing an entity manager "+entityManager);
        EntityTransaction et = entityManager.getTransaction();
        if(et.getRollbackOnly()) {
            et.rollback();
        }
        else {
            et.commit();
        }
        entityManager.close();
    }
}
