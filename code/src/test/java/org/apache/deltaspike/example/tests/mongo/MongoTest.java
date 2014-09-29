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

package org.apache.deltaspike.example.tests.mongo;

import org.apache.deltaspike.example.mongo.APIHit;
import org.apache.deltaspike.example.mongo.APIHitDAO;
import org.apache.deltaspike.example.mongo.MongoProducer;
import org.apache.deltaspike.example.tests.TestUtils;
import org.apache.deltaspike.example.tests.conf.ExampleConfigSource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.util.Date;

@RunWith(Arquillian.class)
public class MongoTest {
    @Deployment
    public static JavaArchive create() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .addPackage(MongoProducer.class.getPackage())
                .addClass(ExampleConfigSource.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        String[] gavs = {"org.apache.deltaspike.core:deltaspike-core-api",
                "org.apache.deltaspike.core:deltaspike-core-impl"};
        TestUtils.resolveListOfArchives(gavs).forEach(jar::merge);

        return jar;
    }

    @Inject
    private APIHitDAO apiHitDAO;

    @Test
    public void testInsert() {
        APIHit hit = new APIHit();
        hit.setEndTime(new Date());
        hit.setStartTime(new Date());
        hit.setUri("/foo/bar/baz");

        apiHitDAO.insert(hit);

        apiHitDAO.findAll().forEach(h -> System.out.println(h.getId()));
    }
}
