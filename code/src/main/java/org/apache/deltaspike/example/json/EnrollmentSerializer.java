/*
 * Copyright (c) 2013 - 2014 Sparta Systems, Inc.
 */

package org.apache.deltaspike.example.json;

import org.apache.deltaspike.example.jpa.Enrollment;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;

public class EnrollmentSerializer extends JsonSerializer<Enrollment> {
    @Override
    public void serialize(Enrollment enrollment, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();
        if(enrollment.getEnrollmentId() != null) {
            jsonGenerator.writeNumberField("enrollmentId", enrollment.getEnrollmentId());
        }
        jsonGenerator.writeStringField("name",enrollment.getName());
        jsonGenerator.writeEndObject();
    }
}
