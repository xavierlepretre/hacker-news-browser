package com.ycombinator.news.dto;

import com.fasterxml.jackson.databind.JsonMappingException;
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
        assertThat(jobDTO.getId()).isEqualTo(new ItemId(9052645));
        assertThat(jobDTO.getBy()).isEqualTo(new UserId("alivahab"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertThat(dateFormat.format(jobDTO.getTime())).isEqualTo("2015-02-15 22:19:09");
        assertThat(jobDTO.getScore()).isEqualTo(1);
        assertThat(jobDTO.getUrl()).isEqualTo("https://angel.co/chariot/jobs/45415-lead-designer");
        assertThat(jobDTO.getText()).isEqualTo("");
        assertThat(jobDTO.getTitle()).isEqualTo("Chariot (YC W15) Looking for Lead Designer");
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnMissingRequiredTitle() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("job_dto_1_no_title.json"), ItemDTO.class);
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnMissingRequiredText() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("job_dto_1_no_text.json"), ItemDTO.class);
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnMissingRequiredUrl() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("job_dto_1_no_url.json"), ItemDTO.class);
    }
}
