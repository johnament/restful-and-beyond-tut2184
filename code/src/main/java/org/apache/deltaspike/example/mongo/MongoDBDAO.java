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

/**
 * Created by johnament on 9/14/14.
 */

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.apache.log4j.Logger;

import javax.enterprise.inject.Vetoed;
import javax.inject.Inject;
import java.lang.reflect.ParameterizedType;

@Vetoed
public abstract class MongoDBDAO<T extends DBObject> {
    private final Logger logger = Logger.getLogger(MongoDBDAO.class);
    @Inject
    private MongoClient mongoClient;

    public void insert(T t) {
        DBCollection collection = getCollection();
        Object id = collection.insert(t);
        logger.info("The id is: "+id);
        t.put("_id",id);
    }

    public DBCollection getCollection() {
        DB db = this.mongoClient.getDB(getDBName());
        DBCollection collection = db.getCollection(getCollectionName());
        Class<T> clazz = getGenericClass();
        logger.info("Generic class is "+clazz);
        collection.setObjectClass(clazz);
        return collection;
    }

    private Class<T> getGenericClass() {
        return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected abstract String getDBName();

    protected abstract String getCollectionName();
}
