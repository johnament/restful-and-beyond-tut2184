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

package org.apache.deltaspike.example.mongo;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.apache.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import java.lang.reflect.ParameterizedType;

@Vetoed
public abstract class MongoDBDAO<T> {
    @Inject
    private MongoClient mongoClient;

    protected Datastore datastore;

    @PostConstruct
    public void init() {
        Morphia morphia =  new Morphia();
        morphia.map(APIHit.class);
        this.datastore = morphia.createDatastore(mongoClient,getDBName());
    }

    public Object insert(T t) {
        Key<T> result = this.datastore.save(t);
        return result.getId();
    }

    private Class<T> getGenericClass() {
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected abstract String getDBName();
}
