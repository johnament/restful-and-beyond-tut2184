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

package org.apache.deltaspike.example.tests.websocket;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.example.components.annotations.StartsRequestScope;
import org.apache.deltaspike.example.components.interceptor.RequestScopeInterceptor;
import org.apache.deltaspike.example.components.servlet.UndertowComponent;
import org.apache.deltaspike.example.components.websocket.FooClient;
import org.apache.deltaspike.example.components.websocket.FooServer;
import org.apache.deltaspike.example.config.ExampleConfigSource;
import org.apache.deltaspike.example.delegate.Invoker;
import org.apache.deltaspike.example.delegate.RequestInvoker;
import org.apache.deltaspike.example.rest.AdminApplication;
import org.apache.deltaspike.example.websocket.WebSocketDeployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

/**
 * Created by johnament on 9/3/14.
 */
@RunWith(Arquillian.class)
public class WebSocketTest {
    @Deployment
    public static JavaArchive createArchive() {
        String beansXml = "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee\n" +
                "\t\thttp://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\"\n" +
                "       bean-discovery-mode=\"all\">\n" +
                "</beans>";
        String[] gavs = new String[]{"org.apache.deltaspike.core:deltaspike-core-api",
                "org.apache.deltaspike.core:deltaspike-core-impl",
                "org.apache.deltaspike.cdictrl:deltaspike-cdictrl-api",
                "org.apache.deltaspike.cdictrl:deltaspike-cdictrl-weld"};
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "se-examples.jar").addPackage(UndertowComponent.class.getPackage())
                .addPackage(ExampleConfigSource.class.getPackage())
                .addPackage(AdminApplication.class.getPackage())
                .addPackage(FooServer.class.getPackage())
                .addPackage(WebSocketDeployer.class.getPackage())
                .addPackage(UndertowComponent.class.getPackage())
                .addClasses(StartsRequestScope.class, RequestScopeInterceptor.class, Invoker.class, RequestInvoker.class)
                .addAsManifestResource(new StringAsset(beansXml), "beans.xml")
                ;
        Arrays.stream(Maven.resolver().loadPomFromFile("pom.xml")
                .resolve(gavs)
                .withTransitivity().as(JavaArchive.class)).forEach(jar::merge);
//        jar.addAsManifestResource(new StringAsset("org.apache.deltaspike.example.tests.websocket.CDIWebSocketContainerProvider"),
//                "services/javax.websocket.ContainerProvider");
        System.out.println("jar "+jar.toString(true));
        return jar;
    }

    @Inject
    @ConfigProperty(name="undertow.rest.bind.port")
    private Integer undertowBindPort;

    @Inject
    private WebSocketDeployer deployer;

    @Test
    public void testCreateConnection() throws Exception {
        deployer.startUndertow(null);
        Session session = this.connect("ws://localhost:" + undertowBindPort + "/fooSocket");
        System.out.println("The client "+getClass()+" connected with session id "+session.getId());
        for(int i = 0;i<5;i++) {
            session.getBasicRemote().sendText("fim "+i);
        }
        Thread.sleep(2*1000);
    }

    public Session connect(String uri) throws IOException, DeploymentException {
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        URI endpointURI = URI.create(uri);
        return webSocketContainer.connectToServer(FooClient.class,endpointURI);
    }
}
