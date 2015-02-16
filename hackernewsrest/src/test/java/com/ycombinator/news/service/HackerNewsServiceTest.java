package com.ycombinator.news.service;

import com.ycombinator.news.dto.ItemId;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class HackerNewsServiceTest
{
    @Test public void testNoSplitOn1()
    {
        List<ItemId> itemIds = Arrays.asList(new ItemId(1), new ItemId(2), new ItemId(3));

        List<List<ItemId>> split = HackerNewsService.splitList(itemIds, 1);
        assertThat(split.size()).isEqualTo(1);
        assertThat(split.get(0)).isEqualTo(itemIds);
    }

    @Test public void testSplitOn2()
    {
        List<ItemId> itemIds = Arrays.asList(new ItemId(1), new ItemId(2), new ItemId(3));

        List<List<ItemId>> split = HackerNewsService.splitList(itemIds, 2);
        assertThat(split.size()).isEqualTo(2);
        assertThat(split.get(0)).isEqualTo(Arrays.asList(new ItemId(1), new ItemId(3)));
        assertThat(split.get(1)).isEqualTo(Arrays.asList(new ItemId(2)));
    }

    @Test public void testSplitOn3()
    {
        List<ItemId> itemIds = Arrays.asList(new ItemId(1), new ItemId(2), new ItemId(3), new ItemId(4), new ItemId(5));

        List<List<ItemId>> split = HackerNewsService.splitList(itemIds, 3);
        assertThat(split.size()).isEqualTo(3);
        assertThat(split.get(0)).isEqualTo(Arrays.asList(new ItemId(1), new ItemId(4)));
        assertThat(split.get(1)).isEqualTo(Arrays.asList(new ItemId(2), new ItemId(5)));
        assertThat(split.get(2)).isEqualTo(Arrays.asList(new ItemId(3)));
    }
}
