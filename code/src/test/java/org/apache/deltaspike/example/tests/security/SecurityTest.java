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

package org.apache.deltaspike.example.tests.security;

import org.apache.deltaspike.example.components.undertow.UndertowComponent;
import org.apache.deltaspike.example.tests.conf.ExampleConfigSource;
import org.apache.deltaspike.example.security.LoginRestFilter;
import org.apache.deltaspike.example.tests.deployers.SecureServer;
import org.apache.log4j.BasicConfigurator;
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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Base64;

/**
 * Created by johnament on 9/7/14.
 */
@RunWith(Arquillian.class)
public class SecurityTest {
    @Deployment
    public static JavaArchive createArchive() {
        BasicConfigurator.configure();
        String beansXml = "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee\n" +
                "\t\thttp://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\"\n" +
                "       bean-discovery-mode=\"all\">\n" +
                "<interceptors>\n" +
                "        <class>org.apache.deltaspike.security.impl.extension.SecurityInterceptor</class>\n" +
                "    </interceptors>\n"+
                "</beans>";
        String[] gavs = new String[]{"org.apache.deltaspike.core:deltaspike-core-api",
                "org.apache.deltaspike.core:deltaspike-core-impl",
                "org.apache.deltaspike.modules:deltaspike-security-module-api",
                "org.apache.deltaspike.modules:deltaspike-security-module-impl",
                "org.apache.deltaspike.cdictrl:deltaspike-cdictrl-api",
                "org.apache.deltaspike.cdictrl:deltaspike-cdictrl-weld"};
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "se-examples.jar").addPackage(UndertowComponent.class.getPackage())
                .addPackage(ExampleConfigSource.class.getPackage())
                .addPackage(LoginRestFilter.class.getPackage())
                .addAsManifestResource(new StringAsset(beansXml), "beans.xml")
                .addClass(SecureServer.class);
        Arrays.stream(Maven.resolver().offline().loadPomFromFile("pom.xml")
                .resolve(gavs)
                .withTransitivity().as(JavaArchive.class)).forEach(jar::merge);
        return jar;
    }

    @Inject
    private SecureServer secureServer;

    @Test
    public void testGeneralCall() {
        secureServer.startUndertow(null);
        Client client = ClientBuilder.newClient();
        Response response = client.target("http://localhost:8989/secured/foo").request().get();
        Assert.assertEquals("401 was expected", 401, response.getStatus());
        response.close();
    }

    @Test
    public void testAuthedCall() throws Exception {
        String credential = "Basic "+Base64.getEncoder().encodeToString("admin:admin".getBytes("UTF-8"));
        Client client = ClientBuilder.newClient();
        Response response = client.target("http://localhost:8989/secured/foo").request()
                .header("Authorization", credential).get();
        String entity = response.readEntity(String.class);

        response.close();
        Assert.assertEquals("200 was expected", 200, response.getStatus());
        Assert.assertEquals("Hello, admin!",entity);
        secureServer.shutdown();
    }
}
