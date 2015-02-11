package com.ycombinator.news.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.service.HackerNewsRestAdapter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class JobDTOTest
{
    private ObjectMapper mapper;

    @Before public void setUp()
    {
        this.mapper = HackerNewsRestAdapter.createHackerNewsMapper();
    }

    @Test public void testDeserialisePopulatesStory() throws IOException
    {
        ItemDTO itemDTO = mapper.readValue(getClass().getResourceAsStream("job_dto_1.json"), ItemDTO.class);

        assertThat(itemDTO).isInstanceOf(JobDTO.class);
        JobDTO jobDTO = (JobDTO) itemDTO;
        assertThat(jobDTO.getId()).isEqualTo(new ItemId(126809));
        assertThat(jobDTO.getBy()).isEqualTo(new UserId("pg"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertThat(dateFormat.format(jobDTO.getTime())).isEqualTo("2008-03-02 04:34:12");
    }
}
