package com.ycombinator.news.dto;

import android.content.Intent;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

public class ItemDTOUtilTest extends AndroidTestCase
{
    @SmallTest
    public void testItemIntentReturnsWithId()
    {
        Intent intent = ItemDTOUtil.createItemBrowserIntent(new OpenItemDTO(new ItemId(1501), new UserId("fgtr"), new Date()));
        assertThat(intent.getData().toString()).contains("1501");
    }

    @SmallTest
    public void testJobIntentReturnsWithField()
    {
        Intent intent = ItemDTOUtil.createItemBrowserIntent(new OpenJobDTO(new ItemId(1), new UserId("a"), new Date(), "title", "http://urlshouldbe.ok/here2", 32, "text"));
        assertThat(intent.getData().toString()).contains("http://urlshouldbe.ok/here2");
    }

    @SmallTest
    public void testStoryIntentReturnsWithField()
    {
        Intent intent = ItemDTOUtil.createItemBrowserIntent(new OpenStoryDTO(new ItemId(1), new UserId("a"), new Date(), "title", "http://urlshouldbe.ok/here", 32, null));
        assertThat(intent.getData().toString()).contains("http://urlshouldbe.ok/here");
    }
}
