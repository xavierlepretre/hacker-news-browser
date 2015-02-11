package com.ycombinator.news.dto;

import android.annotation.SuppressLint;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.service.HackerNewsRestAdapter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ItemDTOTest
{
    private ObjectMapper mapper;

    @Before public void setUp()
    {
        this.mapper = HackerNewsRestAdapter.createHackerNewsMapper();
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
        mapper.readValue(getClass().getResourceAsStream("comment_dto_1_no_by.json"), ItemDTO.class);
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnMissingRequiredTime() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("comment_dto_1_no_time.json"), ItemDTO.class);
    }
}
