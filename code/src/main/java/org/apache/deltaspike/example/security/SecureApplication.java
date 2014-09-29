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

package org.apache.deltaspike.example.security;

import com.google.common.collect.Sets;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("/")
@ApplicationScoped
public class SecureApplication extends Application {

    @Inject
    private ResteasyCdiExtension resteasyCdiExtension;

    @Override
    public Set<Class<?>> getClasses() {
        // should be empty, but just in case
            Set<Class<?>> classes = super.getClasses();
        Set<Class<?>> resultClasses = Sets.newHashSet();
        resultClasses.addAll(classes);
        resteasyCdiExtension.getProviders().forEach(resultClasses::add);
        resteasyCdiExtension.getResources().stream().filter(r ->{

                Path p = (Path) r.getAnnotation(Path.class);
                return p.value().contains("secure");

        }).forEach(resultClasses::add);

        return resultClasses;
    }
}