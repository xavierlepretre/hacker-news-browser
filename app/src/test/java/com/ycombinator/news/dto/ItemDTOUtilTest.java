package com.ycombinator.news.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.service.HackerNewsMapper;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class ItemDTOUtilTest
{
    ObjectMapper objectMapper;

    @Before public void setUp()
    {
        objectMapper = HackerNewsMapper.createHackerNewsMapper();
    }

    @Test public void testActionBarTitleJob() throws IOException
    {
        assertThat(ItemDTOUtil.getActionBarTitle(objectMapper.readValue(
                getClass().getResourceAsStream("job_dto_1.json"),
                ItemDTO.class)))
                .isEqualTo("Chariot (YC W15) Looking for Lead Designer");
    }

    @Test public void testActionBarTitleStory() throws IOException
    {
        assertThat(ItemDTOUtil.getActionBarTitle(objectMapper.readValue(
                getClass().getResourceAsStream("story_dto_1.json"),
                ItemDTO.class)))
                .isEqualTo("My YC app: Dropbox - Throw away your USB drive");
    }

    @Test public void testActionBarTitlePoll() throws IOException
    {
        assertThat(ItemDTOUtil.getActionBarTitle(objectMapper.readValue(
                getClass().getResourceAsStream("poll_dto_1.json"),
                ItemDTO.class)))
                .isEqualTo("Poll: What would happen if News.YC had explicit support for polls?");
    }

    @Test public void testActionBarTitlePollOpt() throws IOException
    {
        assertThat(ItemDTOUtil.getActionBarTitle(objectMapper.readValue(
                getClass().getResourceAsStream("poll_opt_dto_1.json"),
                ItemDTO.class)))
                .isEqualTo("Yes, ban them; I'm tired of seeing Valleywag stories on News.YC.");
    }

    @Test public void testActionBarTitleComment() throws IOException
    {
        assertThat(ItemDTOUtil.getActionBarTitle(objectMapper.readValue(
                getClass().getResourceAsStream("comment_dto_1.json"),
                ItemDTO.class)))
                .isEqualTo(
                        "Aw shucks, guys ... you make me blush with your compliments.<p>Tell you what, Ill make a deal: I'll keep writing if you keep reading. K?");
    }

    @Test public void testActionBarTitleItem() throws IOException
    {
        assertThat(ItemDTOUtil.getActionBarTitle(objectMapper.readValue(
                getClass().getResourceAsStream("item_dto_1.json"),
                ItemDTO.class)))
                .isEqualTo("9,052,645");
    }

    @Test public void testActionBarTitleTitledBeforeTexted()
    {
        ItemDTO mocked = mock(ItemDTO.class, withSettings().extraInterfaces(
                TitledDTO.class, TextedDTO.class));
        when(((TitledDTO) mocked).getTitle()).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                return "some title";
            }
        });
        when(((TextedDTO) mocked).getText()).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                return "some text";
            }
        });
        assertThat(ItemDTOUtil.getActionBarTitle(mocked)).isEqualTo("some title");
    }

    @Test public void testCopyableTextJob() throws IOException
    {
        assertThat(ItemDTOUtil.getCopyableText(objectMapper.readValue(
                getClass().getResourceAsStream("job_dto_1.json"),
                ItemDTO.class)))
                .isEqualTo("No text");
    }

    @Test public void testCopyableTextStory() throws IOException
    {
        assertThat(ItemDTOUtil.getCopyableText(objectMapper.readValue(
                getClass().getResourceAsStream("story_dto_1.json"),
                ItemDTO.class)))
                .isEqualTo("");
    }

    @Test public void testCopyableTextPoll() throws IOException
    {
        assertThat(ItemDTOUtil.getCopyableText(objectMapper.readValue(
                getClass().getResourceAsStream("poll_dto_1.json"),
                ItemDTO.class)))
                .isEqualTo("No text");
    }

    @Test public void testCopyableTextPollOpt() throws IOException
    {
        assertThat(ItemDTOUtil.getCopyableText(objectMapper.readValue(
                getClass().getResourceAsStream("poll_opt_dto_1.json"),
                ItemDTO.class)))
                .isEqualTo("Yes, ban them; I'm tired of seeing Valleywag stories on News.YC.");
    }

    @Test public void testCopyableTextComment() throws IOException
    {
        assertThat(ItemDTOUtil.getCopyableText(objectMapper.readValue(
                getClass().getResourceAsStream("comment_dto_1.json"),
                ItemDTO.class)))
                .isEqualTo(
                        "Aw shucks, guys ... you make me blush with your compliments.<p>Tell you what, Ill make a deal: I'll keep writing if you keep reading. K?");
    }

    @Test public void testCopyableTextItem() throws IOException
    {
        assertThat(ItemDTOUtil.getCopyableText(objectMapper.readValue(
                getClass().getResourceAsStream("item_dto_1.json"),
                ItemDTO.class)))
                .contains("9052645");
    }

    @Test public void testCopyableTextBeforeUrl()
    {
        ItemDTO mocked = mock(ItemDTO.class, withSettings().extraInterfaces(
                TextedDTO.class, WithUrlDTO.class));
        when(((TextedDTO) mocked).getText()).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                return "some text";
            }
        });
        when(((WithUrlDTO) mocked).getUrl()).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                return "some url";
            }
        });
        assertThat(ItemDTOUtil.getCopyableText(mocked)).isEqualTo("some text");
    }

    @Test public void testCopyableTextBeforeTitled()
    {
        ItemDTO mocked = mock(ItemDTO.class, withSettings().extraInterfaces(
                TextedDTO.class, TitledDTO.class));
        when(((TextedDTO) mocked).getText()).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                return "some text";
            }
        });
        when(((TitledDTO) mocked).getTitle()).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                return "some title";
            }
        });
        assertThat(ItemDTOUtil.getCopyableText(mocked)).isEqualTo("some text");
    }

    @Test public void testCopyableUrlBeforeTitled()
    {
        ItemDTO mocked = mock(ItemDTO.class, withSettings().extraInterfaces(
                WithUrlDTO.class, TitledDTO.class));
        when(((WithUrlDTO) mocked).getUrl()).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                return "some url";
            }
        });
        when(((TitledDTO) mocked).getTitle()).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                return "some title";
            }
        });
        assertThat(ItemDTOUtil.getCopyableText(mocked)).isEqualTo("some url");
    }
}
