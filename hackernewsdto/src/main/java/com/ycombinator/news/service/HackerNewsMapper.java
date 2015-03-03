package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.dto.HackerNewsDeserialisingModule;

public class HackerNewsMapper
{
    @NonNull public static ObjectMapper createHackerNewsMapper()
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.registerModule(new HackerNewsDeserialisingModule());
        return mapper;
    }
}
