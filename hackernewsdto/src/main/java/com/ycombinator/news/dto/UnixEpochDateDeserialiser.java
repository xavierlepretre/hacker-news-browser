package com.ycombinator.news.dto;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Date;

public class UnixEpochDateDeserialiser extends StdDeserializer<Date>
{
    protected UnixEpochDateDeserialiser()
    {
        super(Date.class);
    }

    @Override public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        return new Date(1000 * p.readValueAs(Long.class));
    }
}
