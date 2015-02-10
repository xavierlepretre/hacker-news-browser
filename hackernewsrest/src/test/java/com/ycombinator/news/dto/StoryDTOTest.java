package com.ycombinator.news.dto;

import android.annotation.SuppressLint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.service.HackerNewsRestAdapter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class StoryDTOTest
{
    private ObjectMapper mapper;

    @Before public void setUp()
    {
        this.mapper = HackerNewsRestAdapter.createHackerNewsMapper();
    }

    @Test public void testDeserialisePopulatesStory() throws IOException
    {
        ItemDTO itemDTO = mapper.readValue(getClass().getResourceAsStream("story_dto_1.json"), ItemDTO.class);

        assertThat(itemDTO).isInstanceOf(StoryDTO.class);
        StoryDTO storyDTO = (StoryDTO) itemDTO;
        assertThat(storyDTO.getId()).isEqualTo(new ItemId(8863));
        assertThat(storyDTO.getBy()).isEqualTo(new UserId("dhouston"));
        assertThat(storyDTO.getUrl()).isEqualTo("http://www.getdropbox.com/u/2/screencast.html");
        assertThat(storyDTO.getScore()).isEqualTo(111);
        assertThat(storyDTO.getKids().size()).isEqualTo(33);
        assertThat(storyDTO.getKids().get(3)).isEqualTo(new ItemId(8884));
        assertThat(storyDTO.getTitle()).isEqualTo("My YC app: Dropbox - Throw away your USB drive");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertThat(dateFormat.format(storyDTO.getTime())).isEqualTo("2007-04-05 03:16:40");
    }
}
