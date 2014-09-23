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

import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.example.tests.conf.ExampleConfigSource;
import org.apache.deltaspike.example.config.LogSetup;
import org.apache.deltaspike.example.tests.employees.Employees;
import org.apache.deltaspike.example.tests.employees.ShortPersistence;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.CDI;
import javax.persistence.EntityManager;
import java.util.Arrays;

/**
 * Tests JPA functionality within the app.
 */
@RunWith(Arquillian.class)
public class JPAShortTest {
    @Deployment
    public static JavaArchive createArchive() {
        LogSetup.configureLogger();
        String beansXml = "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee\n" +
                "\t\thttp://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\"\n" +
                "       bean-discovery-mode=\"all\">\n" +
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
                .addClasses(Employees.class, ShortPersistence.class);

        Arrays.stream(Maven.resolver().offline().loadPomFromFile("pom.xml")
                .resolve(gavs)
                .withTransitivity().as(JavaArchive.class)).forEach(jar::merge);


        jar.addAsManifestResource(new StringAsset(beansXml), "beans.xml");
        return jar;
    }

    @Test
    public void testCreateEmployee() {
        ContextControl contextControl = CDI.current().select(ContextControl.class).get();
        contextControl.startContext(RequestScoped.class);
        Employees e = new Employees();
        e.setFirstName("Bob");
        e.setLastName("Hope");
        EntityManager em = CDI.current().select(EntityManager.class).get();
        Employees e2 = em.merge(e);
        System.out.println("Employee id :"+e2.getId());
        contextControl.stopContext(RequestScoped.class);
    }
}
