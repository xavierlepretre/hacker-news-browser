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

public class CommentDTOTest
{
    private ObjectMapper mapper;

    @Before public void setUp()
    {
        this.mapper = HackerNewsMapper.createHackerNewsMapper();
    }

    @Test public void testDeserialisePopulatesStory() throws IOException
    {
        ItemDTO itemDTO = mapper.readValue(getClass().getResourceAsStream("comment_dto_1.json"), ItemDTO.class);

        assertThat(itemDTO).isInstanceOf(CommentDTO.class);
        CommentDTO commentDTO = (CommentDTO) itemDTO;
        assertThat(commentDTO.getId()).isEqualTo(new ItemId(2921983));
        assertThat(commentDTO.getBy()).isEqualTo(new UserId("norvig"));
        assertThat(commentDTO.getParent()).isEqualTo(new ItemId(2921506));
        assertThat(commentDTO.getKids().size()).isEqualTo(7);
        assertThat(commentDTO.getKids().get(3)).isEqualTo(new ItemId(2922709));
        assertThat(commentDTO.getText()).isEqualTo("Aw shucks, guys ... you make me blush with your compliments.<p>Tell you what, Ill make a deal: I'll keep writing if you keep reading. K?");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertThat(dateFormat.format(commentDTO.getTime())).isEqualTo("2011-08-25 02:38:47");
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnCommentMissingRequiredParent() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("comment_dto_1_no_parent.json"), ItemDTO.class);
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnCommentMissingRequiredText() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("comment_dto_1_no_text.json"), ItemDTO.class);
    }

    @Test(expected = JsonMappingException.class)
    public void testFailsOnCommentMissingByButNotDeleted() throws IOException
    {
        mapper.readValue(getClass().getResourceAsStream("comment_dto_1_no_by.json"), ItemDTO.class);
    }

    @Test()
    public void testOnCommentMissingTextAndDeleted() throws IOException
    {
        ItemDTO itemDTO = mapper.readValue(getClass().getResourceAsStream("comment_dto_1_no_text_deleted.json"), ItemDTO.class);

        assertThat(itemDTO).isInstanceOf(CommentDTO.class);
        CommentDTO commentDTO = (CommentDTO) itemDTO;
        assertThat(commentDTO.getId()).isEqualTo(new ItemId(2921983));
        assertThat(commentDTO.getBy()).isEqualTo(new UserId("norvig"));
        assertThat(commentDTO.getParent()).isEqualTo(new ItemId(2921506));
        assertThat(commentDTO.getKids().size()).isEqualTo(7);
        assertThat(commentDTO.getKids().get(3)).isEqualTo(new ItemId(2922709));
        assertThat(commentDTO.getText()).isEqualTo("deleted");
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        assertThat(dateFormat.format(commentDTO.getTime())).isEqualTo("2011-08-25 02:38:47");
    }
}
