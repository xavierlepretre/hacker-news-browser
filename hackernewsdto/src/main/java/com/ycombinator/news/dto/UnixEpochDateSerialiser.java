package com.ycombinator.news.dto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Date;

public class UnixEpochDateSerialiser extends JsonSerializer<Date>
{
    public UnixEpochDateSerialiser()
    {
    }

    @Override public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException
    {
        gen.writeNumber(value.getTime() / 1000);
    }
}
