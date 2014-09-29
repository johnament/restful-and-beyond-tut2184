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

package org.apache.deltaspike.example.components.undertow;

import io.undertow.servlet.api.ClassIntrospecter;
import io.undertow.servlet.api.InstanceFactory;
import io.undertow.servlet.api.InstanceHandle;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;

@ApplicationScoped
public class CDIClassIntrospecter implements ClassIntrospecter {
    @Override
    public <T> InstanceFactory<T> createInstanceFactory(Class<T> aClass) throws NoSuchMethodException {
        return new CDIInstanceFactory<>(aClass);
    }

    private static class CDIInstanceFactory<T> implements InstanceFactory<T> {
        private Class<T> aClass;
        public CDIInstanceFactory(Class<T> aClass) {
            this.aClass = aClass;
        }
        @Override
        public InstanceHandle<T> createInstance() throws InstantiationException {
            return new CDIInstanceHandle<>(aClass);
        }
    }

    private static class CDIInstanceHandle<T> implements InstanceHandle<T> {
        private Class<T> aClass;
        private T instance;
        public CDIInstanceHandle(Class<T> aClass) {
            this.aClass = aClass;
            this.instance = CDI.current().select(aClass).get();
        }
        @Override
        public T getInstance() {
            return instance;
        }

        @Override
        public void release() {

        }
    }
}
