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

/**
 * A literal implementation of WebInitParam for programmatic use.
 */
public class WebInitParamLiteral extends AnnotationLiteral<WebInitParam> implements WebInitParam {
    private String name;
    private String value;
    public WebInitParamLiteral(String name, String value) {
        this.name = name;
        this.value = value;
    }
    @Override
    public String name() {
        return name;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String description() {
        return null;
    }
}
