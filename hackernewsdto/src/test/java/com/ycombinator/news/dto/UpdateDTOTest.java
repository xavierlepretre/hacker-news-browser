package com.ycombinator.news.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.service.HackerNewsMapper;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class UpdateDTOTest
{
    private ObjectMapper mapper;

    @Before public void setUp()
    {
        this.mapper = HackerNewsMapper.createHackerNewsMapper();
    }

    @Test public void testDeserialisePopulatesStory() throws IOException
    {
        UpdateDTO updateDTO = mapper.readValue(getClass().getResourceAsStream("update_dto_1.json"), UpdateDTO.class);

        assertThat(updateDTO).isInstanceOf(UpdateDTO.class);
        assertThat(updateDTO.getItems().size()).isEqualTo(29);
        assertThat(updateDTO.getItems().get(4)).isEqualTo(new ItemId(8423178));
        assertThat(updateDTO.getProfiles().size()).isEqualTo(32);
        assertThat(updateDTO.getProfiles().get(1)).isEqualTo(new UserId("mdda"));
    }
}
