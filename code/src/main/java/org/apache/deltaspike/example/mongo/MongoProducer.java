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

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.apache.deltaspike.core.api.config.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by johnament on 9/14/14.
 */
@ApplicationScoped
public class MongoProducer {
    @Inject
    @ConfigProperty(name="mongo.connections")
    private String mongoConnectionInfo;
    private MongoClient mongoClient;
    public void init(@Observes @Initialized(ApplicationScoped.class) Object object) {

    }

    @PostConstruct
    public void start() {
        List<ServerAddress> serverAddressList = new ArrayList<>();
        Arrays.stream(mongoConnectionInfo.split(";")).forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                String[] hostAndPort = s.split(":");
                String host = hostAndPort[0];
                int port = Integer.parseInt(hostAndPort[1]);
                try {
                    serverAddressList.add(new ServerAddress(host,port));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });
        this.mongoClient = new MongoClient(serverAddressList);
    }
    @Produces
    @ApplicationScoped
    public MongoClient client() {
        return this.mongoClient;
    }
    @PreDestroy
    public void shutdown() {
        this.mongoClient.close();
    }
}
