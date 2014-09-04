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

package org.apache.deltaspike.example.components.interceptor;

import org.apache.deltaspike.cdise.api.ContextControl;

import org.apache.deltaspike.example.components.annotations.StartsRequestScope;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@StartsRequestScope
@Priority(100)
public class RequestScopeInterceptor {

    @Inject
    private BeanManager beanManager;

    private boolean isRequestScopeActive() {
        try {
            return beanManager.getContext(RequestScoped.class).isActive();
        } catch (Exception e) {
            return false;
        }
    }

    @AroundInvoke
    public Object startRequestScope(final InvocationContext ctx) throws Exception {
        Object result = null;
        ContextControl contextControl = null;
        if(!isRequestScopeActive()) {
            contextControl = CDI.current().select(ContextControl.class).get();
            contextControl.startContext(RequestScoped.class);
        }
        try {
            result = ctx.proceed();
        }
        finally {
            if(contextControl != null) {
                contextControl.stopContext(RequestScoped.class);
            }
        }
        return result;
    }
}
