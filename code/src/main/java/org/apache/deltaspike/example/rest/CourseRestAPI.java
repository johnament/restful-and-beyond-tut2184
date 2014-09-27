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

package org.apache.deltaspike.example.rest;

import org.apache.deltaspike.example.jpa.Course;
import org.apache.deltaspike.example.jpa.CourseRepository;
import org.apache.deltaspike.example.jpa.Enrollment;
import org.apache.deltaspike.example.jpa.EnrollmentRepository;
import org.apache.deltaspike.example.security.CourseCreateBinding;
import org.apache.deltaspike.example.socket.ClientConnectionComponent;
import org.apache.deltaspike.jpa.api.transaction.Transactional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Set;

/**
 * Created by johnament on 9/21/14.
 */
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/courses")
@Transactional
public class CourseRestAPI {
    @Inject
    private CourseRepository courseRepository;

    @Inject
    private EnrollmentRepository enrollmentRepository;

    @Inject
    private Validator validator;

    @Inject
    private ClientConnectionComponent clientConnectionComponent;

    @GET
    public Courses findAllCourses() {
        Courses courses = new Courses();
        courses.setCourses(courseRepository.listAllCourses());
        return courses;
    }

    @GET
    @Path("/{courseId}")
    public Course findCourse(@PathParam("courseId") Integer courseId) {
        return courseRepository.findCourse(courseId);
    }

    @GET
    @Path("/{courseId}/enrollments")
    public Enrollments findByCourse(@PathParam("courseId") Integer courseId) {
        Course course = courseRepository.findCourse(courseId);
        Enrollments enrollments = new Enrollments();
        enrollments.setEnrollmentList(course.getEnrollmentList());
        return enrollments;
    }

    @POST
    @Path("/{courseId}/enrollments")
    public Enrollment enroll(@PathParam("courseId") Integer courseId,
                             Enrollment enrollment) {
        Course course = courseRepository.findCourse(courseId);
        enrollment.setCourse(course);
        return this.enrollmentRepository.save(enrollment);
    }

//    @POST
//    @Path("/{courseId}/enrollments")
//    public Enrollment enroll(@PathParam("courseId") Integer courseId,
//                             Enrollment enrollment) {
//        Course course = courseRepository.findCourse(courseId);
//        Set<ConstraintViolation<Course>> courseConstraints = validator.validate(course, Course.NewEnrollment.class);
//        if(courseConstraints.isEmpty()) {
//            enrollment.setCourse(course);
//            if(course.getEnrollmentList().size() == 4) {
//                String msg = String.format("Course %s has reached max enrollments",courseId);
//                this.clientConnectionComponent.notifyAllSessions(msg);
//            }
//            return this.enrollmentRepository.save(enrollment);
//        }
//        else {
//            StringBuilder messages = new StringBuilder();
//            for(ConstraintViolation<Course> cv : courseConstraints) {
//                messages.append(cv.getMessage());
//            }
//            throw new RuntimeException(messages.toString());
//        }
//    }

    @PUT
    @Path("/{courseId}")
    public Course updateCourse(@PathParam("courseId") Integer courseId, Course course) {
        return courseRepository.save(course);
    }

    @POST
    @CourseCreateBinding
    public Course createCourse(Course course) {
        String msg = String.format("New course %s has been created",course.getName());
        Course c = courseRepository.save(course);
        this.clientConnectionComponent.notifyAllSessions(msg);
        return c;
    }
}
