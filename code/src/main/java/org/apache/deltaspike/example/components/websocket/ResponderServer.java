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

package org.apache.deltaspike.example.components.websocket;

import org.apache.deltaspike.example.components.annotations.StartsRequestScope;
import org.apache.deltaspike.example.delegate.RequestInvoker;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * Created by johnament on 9/3/14.
 */
@ApplicationScoped
@ServerEndpoint("/serverSocket")
public class ResponderServer {
    @PostConstruct
    public void init() {
        System.out.println("Created server...");
    }
    @OnMessage
    @StartsRequestScope
    public void respond(String data, Session session) {
        System.out.println("Server Received "+data);
        try {
            CDI.current().select(RequestInvoker.class).get().inRequestScope();
            session.getBasicRemote().sendText("foo "+data);

        } catch (Exception e) { }
    }
}