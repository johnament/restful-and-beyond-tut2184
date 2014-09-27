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

package org.apache.deltaspike.example.tests.application.arq;

import org.apache.deltaspike.example.Listener;
import org.apache.deltaspike.example.components.annotations.StartsRequestScope;
import org.apache.deltaspike.example.components.interceptor.RequestScopeInterceptor;
import org.apache.deltaspike.example.components.servlet.WebFilterLiteral;
import org.apache.deltaspike.example.components.undertow.UndertowComponent;
import org.apache.deltaspike.example.components.websocket.ResponderServer;
import org.apache.deltaspike.example.config.AppConfig;
import org.apache.deltaspike.example.requestDelegate.RequestInvoker;
import org.apache.deltaspike.example.jpa.Course;
import org.apache.deltaspike.example.jpa.Enrollment;
import org.apache.deltaspike.example.json.CourseSerializer;
import org.apache.deltaspike.example.mongo.APIHit;
import org.apache.deltaspike.example.rest.APILookupResource;
import org.apache.deltaspike.example.rest.Courses;
import org.apache.deltaspike.example.rest.Enrollments;
import org.apache.deltaspike.example.restAdmin.AdminResource;
import org.apache.deltaspike.example.se.ApplicationStartupEvent;
import org.apache.deltaspike.example.security.AccessDeniedExceptionMapper;
import org.apache.deltaspike.example.socket.CourseServer;
import org.apache.deltaspike.example.tests.application.CourseClient;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by johnament on 9/21/14.
 */
@RunWith(Arquillian.class)
public class CourseCrudArgTest {
    @Deployment
    public static JavaArchive createArchive() {
        String beansXml = "<beans xmlns=\"http://xmlns.jcp.org/xml/ns/javaee\"\n" +
                "       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "       xsi:schemaLocation=\"http://xmlns.jcp.org/xml/ns/javaee\n" +
                "\t\thttp://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd\"\n" +
                "       bean-discovery-mode=\"all\">\n" +
                "    <interceptors>\n" +
                "        <class>org.apache.deltaspike.jpa.impl.transaction.TransactionalInterceptor</class>\n" +
                "    </interceptors>\n" +
                "</beans>";
        String[] gavs = new String[]{"org.apache.deltaspike.core:deltaspike-core-api",
                "org.apache.deltaspike.core:deltaspike-core-impl",
                "org.apache.deltaspike.cdictrl:deltaspike-cdictrl-api",
                "org.apache.deltaspike.cdictrl:deltaspike-cdictrl-weld",
                "org.apache.deltaspike.modules:deltaspike-jpa-module-api",
                "org.apache.deltaspike.modules:deltaspike-jpa-module-impl",
                "org.apache.deltaspike.modules:deltaspike-data-module-api",
                "org.apache.deltaspike.modules:deltaspike-data-module-impl"};
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "course-builder.jar")
                .addPackages(false,Listener.class.getPackage(),
                        StartsRequestScope.class.getPackage(),
                        RequestScopeInterceptor.class.getPackage(),
                        WebFilterLiteral.class.getPackage(),
                        UndertowComponent.class.getPackage(),
                        ResponderServer.class.getPackage(),
                        AppConfig.class.getPackage(),
                        RequestInvoker.class.getPackage(),
                        Course.class.getPackage(),
                        CourseSerializer.class.getPackage(),
                        APIHit.class.getPackage(),
                        APILookupResource.class.getPackage(),
                        AdminResource.class.getPackage(),
                        ApplicationStartupEvent.class.getPackage(),
                        AccessDeniedExceptionMapper.class.getPackage(),
                        CourseServer.class.getPackage(),
                        Listener.class.getPackage()
                        )
                .addClass(CourseClient.class)
                .addAsManifestResource(new StringAsset(beansXml), "beans.xml")
                ;
        Maven.resolver().offline().loadPomFromFile("pom.xml")
                .resolve(gavs)
                .withTransitivity()
                .asList(JavaArchive.class)
                .forEach(jar::merge);
        return jar;
    }

    @Inject
    private Listener listener;

    @Test
    public void testCreateCourse() {
        listener.onAppStart(null);
        // given i have created a course
        Course course = new Course();
        course.setName("Course One");
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8787/courses/");
        Response response = target.request().post(Entity.entity(course, MediaType.APPLICATION_JSON_TYPE));
        Course result = response.readEntity(Course.class);
        System.out.println("Result: "+result.getCourseId());
        response.close();

        // when i request that course
        Integer courseId = result.getCourseId();

        WebTarget newTarget = client.target("http://localhost:8787/courses/{courseId}");
        WebTarget targetGetId = newTarget.resolveTemplate("courseId", courseId);

        Response getResp = targetGetId.request(MediaType.APPLICATION_JSON).get();
        Course gotten = getResp.readEntity(Course.class);
        getResp.close();

        // then the IDs are the same
        Assert.assertTrue("Invalid course id "+gotten.getCourseId(),gotten.getCourseId().equals(courseId));
    }

    @Test
    public void testListCourses() {
        listener.onAppStart(null);
        // given i create many courses
        List<Course> courseList = new ArrayList<>();
        courseList.add(new Course("Course A"));
        courseList.add(new Course("Course B"));
        courseList.add(new Course("Course C"));

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8787/courses/");
        courseList.forEach(c -> target.request().post(Entity.entity(c, MediaType.APPLICATION_JSON_TYPE)).close());

        // when i get the list of courses back
        Response listResponse = target.request(MediaType.APPLICATION_JSON).get();
        Courses courses = listResponse.readEntity(Courses.class);
        listResponse.close();
        Assert.assertTrue(courses.getCourses().size() >= courseList.size());
    }

    @Test
    public void testEnrollInNonExistentCourse() throws Exception {
        listener.onAppStart(null);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        URI uri = URI.create("ws://localhost:8787/courseServer");
        Session session = container.connectToServer(CourseClient.class,uri);
        session.setMaxIdleTimeout(50000);
        System.out.println("session "+session.getId());
        Client client = ClientBuilder.newClient();

        WebTarget target = client.target("http://localhost:8787/courses/");
        Course c1 = new Course();
        c1.setName("Course c1");

        Response response = target.request().post(Entity.entity(c1, MediaType.APPLICATION_JSON_TYPE));
        Course result = response.readEntity(Course.class);
        int courseId = result.getCourseId();

        System.out.println("Course : "+courseId);
        response.close();

        WebTarget newTarget = client.target("http://localhost:8787/courses/{courseId}/enrollments");
        WebTarget testCreateId = newTarget.resolveTemplate("courseId", courseId);
        for(int i = 0;i<6;i++) {
            Enrollment enrollment = new Enrollment();
            enrollment.setCourse(result);
            enrollment.setName("John Doe"+i);
            Response postResp = testCreateId.request().post(Entity.entity(enrollment, MediaType.APPLICATION_JSON));
            int respCode = postResp.getStatus();
            if(respCode == 500) {
                String message = postResp.readEntity(String.class);
                Assert.assertEquals("course.enrollments.full",message);
            }
            else {
                Assert.assertEquals(200,respCode);
            }
            postResp.close();
        }

        Response getListResp = testCreateId.request(MediaType.APPLICATION_JSON).get();
        Enrollments enrollments = getListResp.readEntity(Enrollments.class);
        Assert.assertEquals(5,enrollments.getEnrollmentList().size());
        enrollments.getEnrollmentList().forEach(e -> System.out.println("name: "+e.getName()));
        getListResp.close();

        Thread.sleep(500);

        System.out.println("pending messages "+CourseClient.getMessages());
        CourseClient.getMessages().forEach(System.out::println);
    }
}
