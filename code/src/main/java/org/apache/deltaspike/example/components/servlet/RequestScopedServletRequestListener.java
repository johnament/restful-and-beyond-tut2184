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

package org.apache.deltaspike.example.components.servlet;

import org.apache.deltaspike.cdise.api.ContextControl;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A listener that starts request scopes and shuts down when complete.
 */
public class RequestScopedServletRequestListener implements ServletRequestListener {
    private static final Logger LOG = Logger.getLogger(RequestScopedServletRequestListener.class.getName());
    private static final String CDI_REQ_CONTEXT = "cdiRequestContext";

    @Override
    public void requestDestroyed(ServletRequestEvent e)
    {
        LOG.log(Level.FINER,"Request done.");
        ((ContextControl)e.getServletRequest().getAttribute("cdiRequestContext")).stopContext(RequestScoped.class);
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent)
    {
        LOG.log(Level.FINER,"Incoming request.");
        ContextControl contextControl = getContextControl();
        servletRequestEvent.getServletRequest().setAttribute(CDI_REQ_CONTEXT, contextControl);
        contextControl.startContext(RequestScoped.class);
    }

    private ContextControl getContextControl()
    {
        return CDI.current().select(ContextControl.class).get();
    }
}
