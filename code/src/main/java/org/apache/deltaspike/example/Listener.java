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

package org.apache.deltaspike.example;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.example.components.servlet.RequestScopedServletRequestListener;
import org.apache.deltaspike.example.components.servlet.WebServletLiteral;
import org.apache.deltaspike.example.components.undertow.UndertowComponent;
import org.apache.deltaspike.example.rest.CourseApplication;
import org.apache.deltaspike.example.security.UserManager;
import org.apache.deltaspike.example.socket.CourseServer;
import org.apache.log4j.Logger;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class Listener {
    @Inject
    @ConfigProperty(name="http.listen.port")
    private Integer httpListenPort;

    @Inject
    @ConfigProperty(name="http.listen.address")
    private String httpListenAddress;

    @Inject
    @ConfigProperty(name="http.context.root")
    private String contextRoot;

    @Inject
    @ConfigProperty(name="http.deployment.name")
    private String deploymentName;

    @Inject
    private UserManager userManager;

    private static final Logger logger = Logger.getLogger(Listener.class);

    private UndertowComponent undertowComponent;

    public void onAppStart(@Observes @Initialized(ApplicationScoped.class) Object object) {

    }

    @PostConstruct
    public void init() {
        logger.info("Starting undertow w/ resteasy support.");
        WebServlet resteasyServlet = new WebServletLiteral("RestEasy",new String[]{"/"},
                new WebInitParam[]{},true,1);
        Map<String,Object> servletContextParams = new HashMap<>();
        servletContextParams.put(ResteasyDeployment.class.getName(), createDeployment());
        undertowComponent = new UndertowComponent(httpListenPort,httpListenAddress,contextRoot,deploymentName)
                .addServlet(resteasyServlet,HttpServlet30Dispatcher.class)
                .setWebSocketEndpoint(CourseServer.class)
                .addListener(RequestScopedServletRequestListener.class)
                .start(servletContextParams);
        logger.info("Container up and running on port "+httpListenPort);
    }

    private ResteasyDeployment createDeployment() {

        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setInjectorFactoryClass(CdiInjectorFactory.class.getName());
        // by setting the application, we assume the application will list out all resources and providers
        deployment.setApplicationClass(CourseApplication.class.getName());

        return deployment;
    }

    @PreDestroy
    public void shutdown() {
        undertowComponent.stop();
    }
}
