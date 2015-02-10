package com.ycombinator.news.dto;

import android.annotation.SuppressLint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.service.HackerNewsRestAdapter;
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
        this.mapper = HackerNewsRestAdapter.createHackerNewsMapper();
    }


    @Test public void testDeserialisePopulatesUser() throws IOException
    {
        UserDTO userDTO = mapper.readValue(getClass().getResourceAsStream("user_dto_1.json"), UserDTO.class);

        assertThat(userDTO).isNotNull();
        assertThat(userDTO.getId()).isEqualTo(new UserId("jl"));
        assertThat(userDTO.getKarma()).isEqualTo(2937);
        assertThat(userDTO.getAbout()).isEqualTo("This is a test");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertThat(dateFormat.format(userDTO.getCreated())).isEqualTo("2007-03-15 09:50:46");
    }
}
