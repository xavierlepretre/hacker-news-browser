package com.ycombinator.news.dto;

import android.annotation.SuppressLint;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.service.HackerNewsMapper;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class UserDTOTest
{
    private ObjectMapper mapper;

    @Before public void setUp()
    {
        this.mapper = HackerNewsMapper.createHackerNewsMapper();
    }


    @Test public void testDeserialisePopulatesUser() throws IOException
    {
        UserDTO userDTO = mapper.readValue(getClass().getResourceAsStream("user_dto_1.json"), UserDTO.class);

        assertThat(userDTO).isNotNull();
        assertThat(userDTO.getId()).isEqualTo(new UserId("jl"));
        assertThat(userDTO.getKarma()).isEqualTo(2937);
        assertThat(userDTO.getDelay()).isEqualTo(10);
        assertThat(userDTO.getAbout()).isEqualTo("This is a test");
        assertThat(userDTO.getSubmitted().size()).isEqualTo(256);
        assertThat(userDTO.getSubmitted().get(4)).isEqualTo(new ItemId(7699907));
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertThat(dateFormat.format(userDTO.getCreated())).isEqualTo("2007-03-15 09:50:46");
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnUserMissingRequiredId() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("user_dto_1_no_id.json"), ItemDTO.class);
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnUserMissingRequiredCreated() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("user_dto_1_no_created.json"), ItemDTO.class);
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnUserMissingRequiredAbout() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("user_dto_1_no_about.json"), ItemDTO.class);
    }
}
