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

package org.apache.deltaspike.example.tests.deployers;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.example.components.undertow.UndertowComponent;
import org.apache.deltaspike.example.se.ApplicationStartupEvent;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;

/**
 * Deploys undertow with a servlet runtime.
 */
@ApplicationScoped
public class UndertowServletDeployer {

    @Inject
    @ConfigProperty(name="undertow.servlet.bind.port")
    private Integer undertowBindPort;

    @Inject
    @ConfigProperty(name = "undertow.servlet.bind.address")
    private String undertowBindAddress;

    @Inject
    @ConfigProperty(name ="undertow.servlet.context.root")
    private String contextRoot;

    @Inject
    @ConfigProperty(name = "undertow.servlet.deployment.name")
    private String deploymentName;

    private UndertowComponent undertowComponent;

    public void startUndertow(@Observes ApplicationStartupEvent applicationStartupEvent) {
        WebServlet webServlet = GreeterServlet.class.getAnnotation(WebServlet.class);
        undertowComponent = new UndertowComponent(undertowBindPort,undertowBindAddress,contextRoot,deploymentName)
                .addServlet(webServlet,GreeterServlet.class).start();
    }

    @PreDestroy
    public void shutdown() {
        undertowComponent.stop();
    }
}
