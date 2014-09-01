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

package org.apache.deltaspike.example.rest;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.example.components.servlet.RequestScopedServletRequestListener;
import org.apache.deltaspike.example.components.servlet.UndertowComponent;
import org.apache.deltaspike.example.components.servlet.WebServletLiteral;
import org.apache.deltaspike.example.se.ApplicationStartupEvent;
import org.jboss.resteasy.cdi.CdiInjectorFactory;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import java.util.HashMap;
import java.util.Map;

/**
 * Starts undertow with a resteasy configuration.
 */
@ApplicationScoped
public class UndertowRestDeployer {
    @Inject
    @ConfigProperty(name="undertow.rest.bind.port")
    private Integer undertowBindPort;

    @Inject
    @ConfigProperty(name = "undertow.rest.bind.address")
    private String undertowBindAddress;

    @Inject
    @ConfigProperty(name ="undertow.rest.context.root")
    private String contextRoot;

    @Inject
    @ConfigProperty(name = "undertow.rest.deployment.name")
    private String deploymentName;

    @Inject
    private ResteasyCdiExtension resteasyCdiExtension;

    private UndertowComponent undertowComponent;

    public void startUndertow(@Observes ApplicationStartupEvent applicationStartupEvent) {
        WebServlet resteasyServlet = new WebServletLiteral("RestEasy",new String[]{"/"},
                new WebInitParam[]{},true,1);
        Map<String,Object> servletContextParams = new HashMap<>();
        servletContextParams.put(ResteasyDeployment.class.getName(), createDeployment());
        undertowComponent = new UndertowComponent(undertowBindPort,undertowBindAddress,contextRoot,deploymentName)
                .addServlet(resteasyServlet,HttpServlet30Dispatcher.class)
                .addListener(RequestScopedServletRequestListener.class)
                .start(servletContextParams);
    }

    private ResteasyDeployment createDeployment() {

        ResteasyDeployment deployment = new ResteasyDeployment();
        deployment.setInjectorFactoryClass(CdiInjectorFactory.class.getName());
        // by setting the application, we assume the application will list out all resources and providers
        deployment.setApplicationClass(AdminApplication.class.getName());

        return deployment;
    }

    @PreDestroy
    public void shutdown() {
        undertowComponent.stop();
    }
}
