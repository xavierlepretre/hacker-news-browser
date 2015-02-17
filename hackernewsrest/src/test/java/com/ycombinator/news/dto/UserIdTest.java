package com.ycombinator.news.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.service.HackerNewsRestAdapter;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class UserIdTest
{
    private ObjectMapper objectMapper;

    @Before public void setUp()
    {
        this.objectMapper = HackerNewsRestAdapter.createHackerNewsMapper();
    }

    @Test
    public void testCanDeserialiseSerialised() throws IOException
    {
        UserId transformed = objectMapper.readValue(objectMapper.writeValueAsString(new UserId("hgt")), UserId.class);

        assertThat(transformed.id).isEqualTo("hgt");
    }
}
