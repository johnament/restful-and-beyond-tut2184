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

package org.apache.deltaspike.example.tests.persistence;

import org.apache.deltaspike.example.jpa.Employees;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import javax.enterprise.inject.spi.CDI;
import javax.persistence.EntityManager;

/**
 * Created by johnament on 9/11/14.
 */
@Transactional
public class TransactionBean {
    public void createEmployee() {
        Employees e = new Employees();
        e.setFirstName("Bob");
        e.setLastName("Hope");
        EntityManager em = CDI.current().select(EntityManager.class).get();
        Employees e2 = em.merge(e);
        System.out.println("Employee id :" + e2.getId());
    }
}
