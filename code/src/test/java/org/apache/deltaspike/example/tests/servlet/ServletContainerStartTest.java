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

package org.apache.deltaspike.example.tests.servlet;

import org.apache.deltaspike.example.components.undertow.UndertowComponent;
import org.apache.deltaspike.example.config.ExampleConfigSource;
import org.apache.deltaspike.example.servlet.GreeterServlet;
import org.apache.deltaspike.example.servlet.UndertowServletDeployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;

/**
 * Verifies the start up of the servlet listener.
 */
@RunWith(Arquillian.class)
public class ServletContainerStartTest {
    @Deployment
    public static JavaArchive createArchive() {
        String beansXml = "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee\n" +
                "\t\thttp://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\"\n" +
                "       bean-discovery-mode=\"all\">\n" +
                "</beans>";

        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "se-examples.jar").addPackage(UndertowComponent.class.getPackage())
                .addPackage(ExampleConfigSource.class.getPackage()).addPackage(GreeterServlet.class.getPackage())
                .addAsManifestResource(new StringAsset(beansXml),"beans.xml");
        Arrays.stream(Maven.resolver().offline().loadPomFromFile("pom.xml")
                .resolve("org.apache.deltaspike.core:deltaspike-core-api","org.apache.deltaspike.core:deltaspike-core-impl")
                .withTransitivity().as(JavaArchive.class)).forEach(jar::merge);

        return jar;
    }

    @Inject
    private UndertowServletDeployer deployer;

    @Test
    public void testCreate() throws Exception{
        deployer.startUndertow(null);
        URL url = new URL("http://localhost:8989/greet");
        try(InputStream is = url.openStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String text = br.readLine();
            Assert.assertEquals("written",text);
            br.close();
        }
        deployer.shutdown();
    }
}
