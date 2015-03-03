package com.ycombinator.news.dto;

import android.annotation.SuppressLint;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.service.HackerNewsMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ItemDTOTest
{
    private ObjectMapper mapper;

    @Before public void setUp()
    {
        this.mapper = HackerNewsMapper.createHackerNewsMapper();
    }

    @Test(expected = JsonParseException.class)
    public void testFailsOnMissingRequiredType() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("comment_dto_1_no_type.json"), ItemDTO.class);
    }

    @Test
    public void testFallsBackOnUnknownRequiredType() throws IOException
    {
        ItemDTO itemDTO = mapper.readValue(getClass().getResourceAsStream("comment_dto_1_unknown_type.json"), ItemDTO.class);

        assertThat(itemDTO).isExactlyInstanceOf(ItemDTO.class);
        assertThat(itemDTO.getId()).isEqualTo(new ItemId(2921983));
        assertThat(itemDTO.getBy()).isEqualTo(new UserId("norvig"));
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertThat(dateFormat.format(itemDTO.getTime())).isEqualTo("2011-08-25 02:38:47");
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnMissingRequiredItemId() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("comment_dto_1_no_id.json"), ItemDTO.class);
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnMissingRequiredBy() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("job_dto_1_no_by.json"), ItemDTO.class);
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnMissingRequiredTime() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("comment_dto_1_no_time.json"), ItemDTO.class);
    }

    @Test public void testUrlCorrect() throws IOException
    {
        ItemDTO itemDTO = mapper.readValue(getClass().getResourceAsStream("comment_dto_1.json"), ItemDTO.class);

        assertThat(itemDTO.getOwnUrl()).isEqualTo("https://news.ycombinator.com/item?id=2921983");
    }

    @Test
    public void testOnMissingByAndDeleted() throws IOException
    {
        ItemDTO itemDTO = mapper.readValue(getClass().getResourceAsStream("comment_dto_1_no_by_deleted.json"), ItemDTO.class);

        assertThat(itemDTO).isInstanceOf(CommentDTO.class);
        CommentDTO commentDTO = (CommentDTO) itemDTO;
        assertThat(commentDTO.getId()).isEqualTo(new ItemId(2921983));
        assertThat(commentDTO.getBy()).isEqualTo(new UserId("deleted"));
        assertThat(commentDTO.getParent()).isEqualTo(new ItemId(2921506));
        assertThat(commentDTO.getKids().size()).isEqualTo(7);
        assertThat(commentDTO.getKids().get(3)).isEqualTo(new ItemId(2922709));
        assertThat(commentDTO.getText()).isEqualTo("Aw shucks, guys ... you make me blush with your compliments.<p>Tell you what, Ill make a deal: I'll keep writing if you keep reading. K?");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertThat(dateFormat.format(commentDTO.getTime())).isEqualTo("2011-08-25 02:38:47");
    }

    @Test public void testCanDeserialiseSerialised() throws IOException
    {
        ItemDTO original = new ItemDTO(new ItemId(78), new UserId("amf"), new Date(115, 1, 17, 1, 2, 3).getTime() / 1000, false);
        ItemDTO transformed = mapper.readValue(mapper.writeValueAsString(original), ItemDTO.class);

        assertThat(transformed.getId()).isEqualTo(new ItemId(78));
        assertThat(transformed.getBy()).isEqualTo(new UserId("amf"));
        assertThat(transformed.getTime()).isEqualTo(new Date(115, 1, 17, 1, 2, 3));
    }
}