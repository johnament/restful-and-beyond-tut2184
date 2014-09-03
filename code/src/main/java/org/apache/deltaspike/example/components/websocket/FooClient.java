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

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

/**
 * Created by johnament on 9/3/14.
 */
@ApplicationScoped
@ClientEndpoint
public class FooClient {
    private Session session;

    public void connect(String uri) throws IOException, DeploymentException {
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        URI endpointURI = URI.create(uri);
        this.session = webSocketContainer.connectToServer(this,endpointURI);
        System.out.println("The client "+getClass()+" connected with session id "+session.getId());
    }

    public void send(String data) throws IOException {
        this.session.getBasicRemote().sendText(data);
    }

    @OnMessage
    public void receive(String data) {
        System.out.println("Client received "+data);
    }
}
