package com.ycombinator.news.dto;

import android.annotation.SuppressLint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.service.HackerNewsMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class StoryDTOTest
{
    private ObjectMapper mapper;

    @Before public void setUp()
    {
        this.mapper = HackerNewsMapper.createHackerNewsMapper();
    }

    @Test public void testDeserialisePopulatesStory1() throws IOException
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
        assertThat(storyDTO.getDescendants()).isEqualTo(34);
    }

    @Test public void testDeserialisePopulatesStory2() throws IOException
    {
        ItemDTO itemDTO = mapper.readValue(getClass().getResourceAsStream("story_dto_2.json"), ItemDTO.class);

        assertThat(itemDTO).isInstanceOf(StoryDTO.class);
        StoryDTO storyDTO = (StoryDTO) itemDTO;
        assertThat(storyDTO.getId()).isEqualTo(new ItemId(9118122));
        assertThat(storyDTO.getBy()).isEqualTo(new UserId("pmcpinto"));
        assertThat(storyDTO.getUrl()).isEqualTo("http://www.nytimes.com/2015/02/26/health/fast-track-attacks-on-cancer-accelerate-hopes.html");
        assertThat(storyDTO.getScore()).isEqualTo(38);
        assertThat(storyDTO.getKids().size()).isEqualTo(4);
        assertThat(storyDTO.getKids().get(3)).isEqualTo(new ItemId(9118529));
        assertThat(storyDTO.getTitle()).isEqualTo("A Faster Way to Try Many Drugs on Many Cancers");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertThat(dateFormat.format(storyDTO.getTime())).isEqualTo("2015-02-27 18:25:39");
        assertThat(storyDTO.getDescendants()).isEqualTo(98);
    }

    @Test public void testCanDeserialiseSerialised() throws IOException
    {
        StoryDTO original = new StoryDTO(
                new ItemId(78),
                new UserId("amf"),
                new Date(115, 1, 17, 1, 2, 3).getTime() / 1000,
                false,
                "my Title",
                "http://myurl.com",
                12,
                null,
                null,
                1);
        ItemDTO transformed = mapper.readValue(mapper.writeValueAsString(original), ItemDTO.class);

        assertThat(transformed).isExactlyInstanceOf(StoryDTO.class);
        assertThat(transformed.getId()).isEqualTo(new ItemId(78));
        assertThat(transformed.getBy()).isEqualTo(new UserId("amf"));
        assertThat(transformed.getTime()).isEqualTo(new Date(115, 1, 17, 1, 2, 3));
    }

    @Test public void testCanDeserialiseSerialisedList1() throws IOException
    {
        StoryDTO original = new StoryDTO(
                new ItemId(78),
                new UserId("amf"),
                new Date(115, 1, 17, 1, 2, 3).getTime() / 1000,
                false,
                "my Title",
                "http://myurl.com",
                12,
                null,
                null,
                2);
        ItemDTOList list = new ItemDTOList();
        list.add(original);
        List<ItemDTO> transformed = mapper.readValue(mapper.writeValueAsString(list), ItemDTOList.class);

        assertThat(transformed.size()).isEqualTo(1);
        assertThat(transformed.get(0)).isExactlyInstanceOf(StoryDTO.class);
        assertThat(transformed.get(0).getId()).isEqualTo(new ItemId(78));
        assertThat(transformed.get(0).getBy()).isEqualTo(new UserId("amf"));
        assertThat(transformed.get(0).getTime()).isEqualTo(new Date(115, 1, 17, 1, 2, 3));
    }

    @Test public void testCanDeserialiseSerialisedList2() throws IOException
    {
        StoryDTO original1 = new StoryDTO(
                new ItemId(78),
                new UserId("amf"),
                new Date(115, 1, 17, 1, 2, 3).getTime() / 1000,
                false,
                "my Title",
                "http://myurl.com",
                12,
                null,
                null,
                1);
        StoryDTO original2 = new StoryDTO(
                new ItemId(58),
                new UserId("amf"),
                new Date(115, 1, 17, 1, 2, 3).getTime() / 1000,
                false,
                "my Title2",
                "http://myurl2.com",
                12,
                null,
                null,
                2);
        ItemDTOList list = new ItemDTOList();
        list.add(original1);
        list.add(original2);
        List<ItemDTO> transformed = mapper.readValue(mapper.writeValueAsString(list), ItemDTOList.class);

        assertThat(transformed.size()).isEqualTo(2);

        assertThat(transformed.get(0)).isExactlyInstanceOf(StoryDTO.class);
        assertThat(transformed.get(0).getId()).isEqualTo(new ItemId(78));
        assertThat(transformed.get(0).getBy()).isEqualTo(new UserId("amf"));
        assertThat(transformed.get(0).getTime()).isEqualTo(new Date(115, 1, 17, 1, 2, 3));

        assertThat(transformed.get(1)).isExactlyInstanceOf(StoryDTO.class);
        assertThat(transformed.get(1).getId()).isEqualTo(new ItemId(58));
        assertThat(transformed.get(1).getBy()).isEqualTo(new UserId("amf"));
        assertThat(transformed.get(1).getTime()).isEqualTo(new Date(115, 1, 17, 1, 2, 3));
    }
}
