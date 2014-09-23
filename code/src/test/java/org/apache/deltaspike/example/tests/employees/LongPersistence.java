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

package org.apache.deltaspike.example.tests.employees;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * A component for starting and stopping entity managers.
 */
@ApplicationScoped
@Vetoed
public class LongPersistence {
    private static final Logger logger = Logger.getLogger(LongPersistence.class);
    @Inject
    @ConfigProperty(name = "default.persistence.jdbc.url")
    private String url;

    @Inject
    @ConfigProperty(name = "default.persistence.jdbc.user")
    private String user;

    @Inject
    @ConfigProperty(name = "default.persistence.jdbc.password")
    private String password;

    @Inject
    @ConfigProperty(name = "default.persistence.jdbc.driver")
    private String driver;

    @Inject
    @ConfigProperty(name = "default.persistence.jdbc.schemagen")
    private String schemaGen;

    private EntityManagerFactory entityManagerFactory;
    @PostConstruct
    public void init() {
        Map<String,String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.driver",driver);
        properties.put("javax.persistence.jdbc.url",url);
        properties.put("javax.persistence.jdbc.user",user);
        properties.put("javax.persistence.jdbc.password",password);
        properties.put("javax.persistence.schema-generation.database.action",schemaGen);
        entityManagerFactory = Persistence.createEntityManagerFactory("DefaultApp",properties);
        logger.info("Started database connection using entityManagerFactory "+entityManagerFactory);
    }

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
