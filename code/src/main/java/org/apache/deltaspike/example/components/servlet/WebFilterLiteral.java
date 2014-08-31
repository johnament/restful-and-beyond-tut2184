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

import javax.enterprise.util.AnnotationLiteral;
import javax.servlet.DispatcherType;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

/**
 * Created by johnament on 8/30/14.
 */
public class WebFilterLiteral extends AnnotationLiteral<WebFilter> implements WebFilter {
    private WebInitParam[] webInitParams;
    private String[] urlPatterns;
    private boolean asyncSupported;
    private String filterName;
    public WebFilterLiteral(WebInitParam[] params,String[] urlPatterns, boolean asyncSupported, String filterName) {
        this.webInitParams = params;
        this.urlPatterns = urlPatterns;
        this.asyncSupported = asyncSupported;
        this.filterName = filterName;
    }
    @Override
    public String description() {
        return null;
    }

    @Override
    public String displayName() {
        return null;
    }

    @Override
    public WebInitParam[] initParams() {
        return webInitParams;
    }

    @Override
    public String filterName() {
        return filterName;
    }

    @Override
    public String smallIcon() {
        return null;
    }

    @Override
    public String largeIcon() {
        return null;
    }

    @Override
    public String[] servletNames() {
        return new String[0];
    }

    @Override
    public String[] value() {
        return new String[0];
    }

    @Override
    public String[] urlPatterns() {
        return this.urlPatterns;
    }

    @Override
    public DispatcherType[] dispatcherTypes() {
        return new DispatcherType[0];
    }

    @Override
    public boolean asyncSupported() {
        return this.asyncSupported;
    }
}
