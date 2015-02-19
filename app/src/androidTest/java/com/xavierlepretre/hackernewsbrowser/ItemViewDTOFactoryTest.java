package com.xavierlepretre.hackernewsbrowser;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.OpenItemDTO;
import com.ycombinator.news.dto.OpenJobDTO;
import com.ycombinator.news.dto.OpenStoryDTO;
import com.ycombinator.news.dto.UserId;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

public class ItemViewDTOFactoryTest extends AndroidTestCase
{
    @SmallTest
    public void testCreatesStory()
    {
        assertThat(
        ItemViewDTOFactory.create(
                getContext(),
                new OpenStoryDTO(
                        new ItemId(1),
                        new UserId("a"),
                        new Date(),
                        "title",
                        "http://urlshouldbe.ok/here",
                        32,
                        null))).isExactlyInstanceOf(StoryViewDTO.class);
    }

    @SmallTest
    public void testCreatesJob()
    {
        assertThat(
        ItemViewDTOFactory.create(
                getContext(),
                new OpenJobDTO(
                        new ItemId(1),
                        new UserId("a"),
                        new Date(),
                        "title",
                        "url",
                        32,
                        "text"))).isExactlyInstanceOf(JobViewDTO.class);
    }

    @SmallTest
    public void testCreatesDefault()
    {
        assertThat(
        ItemViewDTOFactory.create(
                getContext(),
                new OpenItemDTO(
                        new ItemId(1),
                        new UserId("a"),
                        new Date()))).isExactlyInstanceOf(BaseItemViewDTO.class);
    }
}
