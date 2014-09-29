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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@ApplicationScoped
public class UserManager {
    private Properties users;
    private Properties groups;
    @PostConstruct
    public void init() {
        users = new Properties();
        File f = new File("target/test-classes/users.properties");
        try(FileInputStream fis = new FileInputStream(f)) {
            users.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        groups = new Properties();
        File g = new File("target/test-classes/groups.properties");
        try(FileInputStream fis = new FileInputStream(g)) {
            groups.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void validateCredential(@NotNull @Size(min=1) String username,
                                   @NotNull @Size(min=1) String password) {
        String userPass = users.getProperty(username);
        if(!password.equals(userPass)) {
            throw new UnAuthorizedException("Invalid user "+username);
        }
    }

    public List<String> getGroups(String username) {
        String prop = groups.getProperty(username);
        if(prop == null) {
            return Collections.EMPTY_LIST;
        }
        String[] groups = prop.split(",");
        return Arrays.asList(groups);
    }
}
