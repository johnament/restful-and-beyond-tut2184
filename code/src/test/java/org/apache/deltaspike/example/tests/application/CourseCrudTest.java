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

package org.apache.deltaspike.example.tests.application;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.example.jpa.Course;
import org.apache.deltaspike.example.jpa.Enrollment;
import org.apache.deltaspike.example.rest.Courses;
import org.apache.deltaspike.example.rest.Enrollments;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by johnament on 9/21/14.
 */
public class CourseCrudTest {
    private static CdiContainer cdiContainer;
    private static ContextControl contextControl;

    @BeforeClass
    public static void initWeld() {
        cdiContainer = CdiContainerLoader.getCdiContainer();
        cdiContainer.boot();
        contextControl = cdiContainer.getContextControl();
        contextControl.startContexts();
    }

    @AfterClass
    public static void shutdownWeld() {
        contextControl.stopContexts();
        cdiContainer.shutdown();
    }

    @Test
    public void testCreateCourse() {
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
    public void testEnrollInNonExistentCourse() {
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
        for(int i = 0;i<10;i++) {
            Enrollment enrollment = new Enrollment();
            enrollment.setCourse(result);
            enrollment.setName("John Doe"+i);
            Response postResp = testCreateId.request().post(Entity.entity(enrollment, MediaType.APPLICATION_JSON));
            int respCode = postResp.getStatus();
            System.out.println("Creating resp code " + respCode);
            postResp.close();
        }

        Response getListResp = testCreateId.request(MediaType.APPLICATION_JSON).get();
        Enrollments enrollments = getListResp.readEntity(Enrollments.class);
        Assert.assertEquals(10,enrollments.getEnrollmentList().size());
        enrollments.getEnrollmentList().forEach(e -> System.out.println("name: "+e.getName()));
        getListResp.close();
    }
}
