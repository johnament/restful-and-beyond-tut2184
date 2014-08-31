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
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * Created by johnament on 8/30/14.
 */
public class WebServletLiteral extends AnnotationLiteral<WebServlet> implements WebServlet {
    private String name;
    private String[] urlPatterns;
    private WebInitParam[] params;
    private boolean asyncSupported;
    private int loadOnStartup;
    public WebServletLiteral(String name, String[] urlPatterns, WebInitParam[] params, boolean asyncSupported, int loadOnStartup) {
        this.name = name;
        this.urlPatterns = urlPatterns;
        this.params = params;
        this.asyncSupported = asyncSupported;
        this.loadOnStartup = loadOnStartup;
    }
    @Override
    public String name() {
        return name;
    }

    @Override
    public String[] value() {
        return new String[0];
    }

    @Override
    public String[] urlPatterns() {
        return urlPatterns;
    }

    @Override
    public int loadOnStartup() {
        return loadOnStartup;
    }

    @Override
    public WebInitParam[] initParams() {
        return this.params;
    }

    @Override
    public boolean asyncSupported() {
        return asyncSupported;
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
    public String description() {
        return null;
    }

    @Override
    public String displayName() {
        return null;
    }
}
