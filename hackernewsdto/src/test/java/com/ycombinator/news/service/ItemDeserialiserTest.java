package com.ycombinator.news.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.dto.CommentDTO;
import com.ycombinator.news.dto.ItemDTO;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ItemDeserialiserTest
{
    private ObjectMapper mapper;

    @Before public void setUp()
    {
        this.mapper = HackerNewsMapper.createHackerNewsMapper();
    }

    @Test public void testMapperRobust() throws IOException
    {
        ItemDTO itemDTO = mapper.readValue(getClass().getResourceAsStream("comment_dto_1_too_much.json"), ItemDTO.class);

        assertThat(itemDTO).isInstanceOf(CommentDTO.class);
    }
}
