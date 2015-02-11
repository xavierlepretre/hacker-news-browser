package com.ycombinator.news.dto;

import android.annotation.SuppressLint;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.service.HackerNewsRestAdapter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class PollOptDTOTest
{
    private ObjectMapper mapper;

    @Before public void setUp()
    {
        this.mapper = HackerNewsRestAdapter.createHackerNewsMapper();
    }

    @Test public void testDeserialisePopulatesStory() throws IOException
    {
        ItemDTO itemDTO = mapper.readValue(getClass().getResourceAsStream("poll_opt_dto_1.json"), ItemDTO.class);

        assertThat(itemDTO).isInstanceOf(PollOptDTO.class);
        PollOptDTO pollOptDTO = (PollOptDTO) itemDTO;
        assertThat(pollOptDTO.getId()).isEqualTo(new ItemId(160705));
        assertThat(pollOptDTO.getBy()).isEqualTo(new UserId("pg"));
        assertThat(pollOptDTO.getParent()).isEqualTo(new ItemId(160704));
        assertThat(pollOptDTO.getScore()).isEqualTo(335);
        assertThat(pollOptDTO.getText()).isEqualTo(
                "Yes, ban them; I'm tired of seeing Valleywag stories on News.YC.");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertThat(dateFormat.format(pollOptDTO.getTime())).isEqualTo("2008-04-11 12:02:56");
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnPollOptMissingRequiredParent() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("poll_opt_dto_1_no_parent.json"), ItemDTO.class);
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnPollOptMissingRequiredText() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("poll_opt_dto_1_no_text.json"), ItemDTO.class);
    }
}
