package com.ycombinator.news.dto;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.Date;

public class HackerNewsDeserialisingModule extends SimpleModule
{
    public HackerNewsDeserialisingModule()
    {
        super("HackerNewsDeserialisingModule", new Version(1, 0, 0, null, null, null));
        addDeserializer(Date.class, new UnixEpochDateDeserialiser());
        addSerializer(Date.class, new UnixEpochDateSerialiser());
    }
}
