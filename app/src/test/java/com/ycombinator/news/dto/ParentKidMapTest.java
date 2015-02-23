package com.ycombinator.news.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ParentKidMapTest
{
    ParentKidMap map;

    @Before public void setUp()
    {
        map = new ParentKidMap();
    }

    @After public void tearDown()
    {
        map = null;
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStraightPutFails()
    {
        map.put(new ItemId(1), new ArrayList<ItemId>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStraightPutAllFails()
    {
        map.putAll(new HashMap<ItemId, List<ItemId>>());
    }

    @Test public void testFlattenPrimogenitureOfEmpty()
    {
        assertThat(map.flattenPrimoGeniture(new ItemId(1))).isEmpty();
    }

    @Test public void testAddOneItemStartsAsExpandedAtZero()
    {
        map.put(new ItemDTO(new ItemId(1), new UserId("a"), new Date(), false));
        assertThat(map.getCollapsibleState(new ItemId(1))).isNotNull();
        //noinspection ConstantConditions
        assertThat(map.getCollapsibleState(new ItemId(1)).isCollapsed()).isFalse();
        //noinspection ConstantConditions
        assertThat(map.getCollapsibleState(new ItemId(1)).zeroBasedDepth).isEqualTo(0);
    }

    @Test public void testAddOneChildIsCollapsedAndAtOne()
    {
        map.put(new StoryDTO(new ItemId(1), new UserId("a"), new Date(), false, "title", "url", 123, Arrays.asList(new ItemId(2))));
        map.put(new CommentDTO(new ItemId(2), new UserId("a"), new Date(), false, new ItemId(1), "text", null));
        assertThat(map.getCollapsibleState(new ItemId(2))).isNotNull();
        //noinspection ConstantConditions
        assertThat(map.getCollapsibleState(new ItemId(2)).isCollapsed()).isTrue();
        //noinspection ConstantConditions
        assertThat(map.getCollapsibleState(new ItemId(2)).zeroBasedDepth).isEqualTo(1);
    }

    @Test public void testOneChildOneParentFlattenHasOnlyChild()
    {
        map.put(new StoryDTO(new ItemId(1), new UserId("a"), new Date(), false, "title", "url", 123, Arrays.asList(new ItemId(2))));
        map.put(new CommentDTO(new ItemId(2), new UserId("a"), new Date(), false, new ItemId(1), "text", null));
        assertThat(map.flattenPrimoGeniture(new ItemId(1))).isEqualTo(Arrays.asList(new ItemId(2)));
    }

    @Test public void testOneChildOneParentButCollapsedFlattenHasNothing()
    {
        map.put(new StoryDTO(new ItemId(1), new UserId("a"), new Date(), false, "title", "url", 123, Arrays.asList(new ItemId(2))));
        map.put(new CommentDTO(new ItemId(2), new UserId("a"), new Date(), false, new ItemId(1), "text", null));
        map.toggleCollapsedState(new ItemId(1));
        assertThat(map.flattenPrimoGeniture(new ItemId(1))).isEmpty();
    }
}
