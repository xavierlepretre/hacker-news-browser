package com.ycombinator.news.dto;

import android.content.Intent;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import static org.fest.assertions.api.Assertions.assertThat;

public class ItemDTOUtilTest extends AndroidTestCase
{
    @SmallTest
    public void testItemIntentReturnsWithId()
    {
        Intent intent = ItemDTOUtil.createItemBrowserIntent(new OpenItemDTO(
                new ItemId(1501),
                new UserId("fgtr"),
                123L,
                false));
        assertThat(intent.getData().toString()).contains("1501");
    }

    @SmallTest
    public void testJobIntentReturnsWithField()
    {
        Intent intent = ItemDTOUtil.createItemBrowserIntent(new OpenJobDTO(
                new ItemId(1),
                new UserId("a"),
                123L,
                false,
                "title",
                "http://urlshouldbe.ok/here2",
                32,
                "text"));
        assertThat(intent.getData().toString()).contains("http://urlshouldbe.ok/here2");
    }

    @SmallTest
    public void testStoryIntentReturnsWithField()
    {
        Intent intent = ItemDTOUtil.createItemBrowserIntent(new OpenStoryDTO(
                new ItemId(1),
                new UserId("a"),
                123L,
                false,
                "title",
                "http://urlshouldbe.ok/here",
                32,
                "no text",
                null,
                34));
        assertThat(intent.getData().toString()).contains("http://urlshouldbe.ok/here");
    }

    @SmallTest
    public void testItemCopyReturnsOwnUrl()
    {
        assertThat(ItemDTOUtil.getCopyableText(new OpenItemDTO(
                new ItemId(1501),
                new UserId("fgtr"),
                123L,
                false)))
                .contains("1501");
    }

    @SmallTest
    public void testJobCopyReturnsText()
    {
        assertThat(ItemDTOUtil.getCopyableText(new OpenJobDTO(
                new ItemId(1),
                new UserId("a"),
                123L,
                false,
                "title",
                "http://urlshouldbe.ok/here2",
                32,
                "text")))
                .isEqualTo("text");
    }

    @SmallTest
    public void testStoryCopyReturnsUrl()
    {
        assertThat(ItemDTOUtil.getCopyableText(new OpenStoryDTO(
                new ItemId(1),
                new UserId("a"),
                123L,
                false,
                "title",
                "http://urlshouldbe.ok/here",
                32,
                "story copies text",
                null,
                34)))
                .isEqualTo("story copies text");
    }
}
