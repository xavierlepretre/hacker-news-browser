package com.ycombinator.news.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.service.HackerNewsRestAdapter;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ItemIdTest
{
    private ObjectMapper objectMapper;

    @Before public void setUp()
    {
        this.objectMapper = HackerNewsRestAdapter.createHackerNewsMapper();
    }

    @Test
    public void testCanDeserialiseSerialised() throws IOException
    {
        ItemId transformed = objectMapper.readValue(objectMapper.writeValueAsString(new ItemId(78)), ItemId.class);

        assertThat(transformed.id).isEqualTo(78);
    }
}
