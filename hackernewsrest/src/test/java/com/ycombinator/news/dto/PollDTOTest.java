package com.ycombinator.news.dto;

import android.annotation.SuppressLint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.service.HackerNewsRestAdapter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class PollDTOTest
{
    private ObjectMapper mapper;

    @Before public void setUp()
    {
        this.mapper = HackerNewsRestAdapter.createHackerNewsMapper();
    }

    @Test public void testDeserialisePopulatesStory() throws IOException
    {
        ItemDTO itemDTO = mapper.readValue(getClass().getResourceAsStream("poll_dto_1.json"), ItemDTO.class);

        assertThat(itemDTO).isInstanceOf(PollDTO.class);
        PollDTO pollDTO = (PollDTO) itemDTO;
        assertThat(pollDTO.getId()).isEqualTo(new ItemId(126809));
        assertThat(pollDTO.getBy()).isEqualTo(new UserId("pg"));
        assertThat(pollDTO.getScore()).isEqualTo(46);
        assertThat(pollDTO.getKids().size()).isEqualTo(25);
        assertThat(pollDTO.getKids().get(3)).isEqualTo(new ItemId(126824));
        assertThat(pollDTO.getParts().size()).isEqualTo(3);
        assertThat(pollDTO.getParts().get(1)).isEqualTo(new ItemId(126811));
        assertThat(pollDTO.getTitle()).isEqualTo("Poll: What would happen if News.YC had explicit support for polls?");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertThat(dateFormat.format(pollDTO.getTime())).isEqualTo("2008-03-02 04:34:12");
    }
}
