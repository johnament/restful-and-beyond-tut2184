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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.Base64;

/**
 * Created by johnament on 9/7/14.
 */
@Provider
@RequestScoped
public class LoginRestFilter implements ContainerRequestFilter{
    @Inject
    private User user;

    @Inject
    private UserManager userManager;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String auth = requestContext.getHeaderString("Authorization");
        if(auth != null) {
            String input = auth.replaceFirst("Basic","").trim();
            byte[] credential = Base64.getDecoder().decode(input.getBytes());
            String value = new String(credential,"UTF-8");
            String[] parts = value.split(":");
            userManager.validateCredential(parts[0],parts[1]);
            user.setUsername(parts[0]);
            user.setGroups(userManager.getGroups(parts[0]));
        }
    }
}
