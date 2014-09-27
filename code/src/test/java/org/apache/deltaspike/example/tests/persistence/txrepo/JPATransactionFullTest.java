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

package org.apache.deltaspike.example.tests.persistence.txrepo;

import org.apache.deltaspike.example.jpa.EntityManagerProducer;
import org.apache.deltaspike.example.tests.TestUtils;
import org.apache.deltaspike.example.tests.conf.ExampleConfigSource;
import org.apache.deltaspike.example.config.LogSetup;
import org.apache.deltaspike.example.tests.persistence.EmployeeRepository;
import org.apache.deltaspike.example.jpa.Employees;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by johnament on 9/11/14.
 */
@RunWith(Arquillian.class)
public class JPATransactionFullTest {
    @Deployment
    public static JavaArchive createArchive() {
        LogSetup.configureLogger();
        String interceptor = "    <interceptors>\n" +
                "        <class>org.apache.deltaspike.jpa.impl.transaction.TransactionalInterceptor</class>\n" +
                "    </interceptors>";

        String beansXml = "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee\n" +
                "\t\thttp://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\"\n" +
                "       bean-discovery-mode=\"all\">\n" + interceptor +
                "</beans>";

        String[] gavs = new String[]{"org.apache.deltaspike.core:deltaspike-core-api",
                "org.apache.deltaspike.core:deltaspike-core-impl",
                "org.apache.deltaspike.modules:deltaspike-jpa-module-api",
                "org.apache.deltaspike.modules:deltaspike-jpa-module-impl",
                "org.apache.deltaspike.cdictrl:deltaspike-cdictrl-api",
                "org.apache.deltaspike.cdictrl:deltaspike-cdictrl-weld",
                "org.apache.deltaspike.modules:deltaspike-data-module-api",
                "org.apache.deltaspike.modules:deltaspike-data-module-impl"};
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "se-examples.jar")
                .addPackage(ExampleConfigSource.class.getPackage())
                .addClasses(Employees.class, EntityManagerProducer.class,
                        TransactionBean2.class, EmployeeRepository.class);

        TestUtils.resolveListOfArchives(gavs).forEach(jar::merge);


        jar.delete("META-INF/beans.xml");
        jar.addAsManifestResource(new StringAsset(beansXml), "beans.xml");
        return jar;
    }

    @Inject
    private TransactionBean2 transactionBean2;

    @Test
    public void testCreateEmployee() {
        transactionBean2.createEmployee("Steve","Holt");
        transactionBean2.createEmployee("George Oscar","Bluth");
        transactionBean2.createEmployee("George Michael","Bluth");

        List<Employees> employeesList = transactionBean2.findAll();
        System.out.println("Employee list size: "+employeesList.size());
        employeesList.forEach(e -> System.out.println(e.getFirstName()+" "+e.getLastName()+" id: "+e.getId()));
    }
}
