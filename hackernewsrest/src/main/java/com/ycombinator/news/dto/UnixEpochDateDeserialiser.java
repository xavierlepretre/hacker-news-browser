package com.ycombinator.news.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Date;

public class UnixEpochDateDeserialiser extends StdDeserializer<Date>
{
    private final ObjectMapper objectMapper;

    protected UnixEpochDateDeserialiser()
    {
        super(Date.class);
        this.objectMapper = new ObjectMapper();
    }

    @Override public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException
    {
        return new Date(1000 * p.readValueAs(Long.class));
    }
}
