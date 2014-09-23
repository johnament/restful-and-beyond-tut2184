/*
 * Copyright (c) 2013 - 2014 Sparta Systems, Inc.
 */

package org.apache.deltaspike.example.json;

import org.apache.deltaspike.example.jpa.Course;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

/**
 * Created by johnament on 9/22/14.
 */
public class CourseSerializer extends JsonSerializer<Course> {
    @Override
    public void serialize(Course course, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if(course != null) {
            jsonGenerator.writeStartObject();
            if(course.getCourseId() != null) {
                jsonGenerator.writeNumberField("courseId", course.getCourseId());
            }
            jsonGenerator.writeStringField("name", course.getName());
            jsonGenerator.writeEndObject();
        }
    }
}
