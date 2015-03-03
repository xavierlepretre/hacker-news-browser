package com.ycombinator.news.cache;

import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class QuickCacheImplTest
{
    private QuickCacheImpl quickCache;

    @Before public void setUp()
    {
        quickCache = new QuickCacheImpl(3);
    }

    @After public void tearDown()
    {
        quickCache = null;
    }

    @NonNull ItemDTO mockItemDTO(@NonNull final ItemId id)
    {
        ItemDTO mocked = mock(ItemDTO.class);
        when(mocked.getId()).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                return id;
            }
        });
        return mocked;
    }

    @Test public void testPutLaterCanGet() throws InterruptedException
    {
        ItemDTO mocked = mockItemDTO(new ItemId(98));
        quickCache.put(mocked);
        Thread.sleep(300);
        ItemDTO cached = quickCache.get(new ItemId(98));
        assertThat(cached).isNotNull();
        assertThat(cached).isSameAs(mocked);
    }

    @Test public void testPutAndClearThenCannotGet() throws InterruptedException
    {
        ItemDTO mocked = mockItemDTO(new ItemId(98));
        quickCache.put(mocked);
        Thread.sleep(300);
        quickCache.clear();
        Thread.sleep(300);
        assertThat(quickCache.get(new ItemId(98))).isNull();
    }

    @Test public void testDoublePutOverwrites() throws InterruptedException
    {
        ItemDTO mocked1 = mockItemDTO(new ItemId(98));
        ItemDTO mocked2 = mockItemDTO(new ItemId(98));
        quickCache.put(mocked1);
        quickCache.put(mocked2);
        Thread.sleep(300);
        assertThat(quickCache.get(new ItemId(98))).isSameAs(mocked2);
        assertThat(quickCache.get(new ItemId(98))).isNotSameAs(mocked1);
    }

    @Test public void testPutTooManyThenOldestGetsEvicted() throws InterruptedException
    {
        ItemDTO mocked1 = mockItemDTO(new ItemId(98));
        ItemDTO mocked2 = mockItemDTO(new ItemId(99));
        ItemDTO mocked3 = mockItemDTO(new ItemId(100));
        ItemDTO mocked4 = mockItemDTO(new ItemId(101));
        quickCache.put(mocked1);
        quickCache.put(mocked2);
        quickCache.put(mocked3);
        quickCache.put(mocked4);
        Thread.sleep(300);
        assertThat(quickCache.get(new ItemId(101))).isSameAs(mocked4);
        assertThat(quickCache.get(new ItemId(100))).isSameAs(mocked3);
        assertThat(quickCache.get(new ItemId(99))).isSameAs(mocked2);
        assertThat(quickCache.get(new ItemId(98))).isNull();
    }

    @Test public void testPutManyAndGetThenReordersOldestWhichGetsEvicted() throws InterruptedException
    {
        ItemDTO mocked1 = mockItemDTO(new ItemId(98));
        ItemDTO mocked2 = mockItemDTO(new ItemId(99));
        ItemDTO mocked3 = mockItemDTO(new ItemId(100));
        ItemDTO mocked4 = mockItemDTO(new ItemId(101));
        quickCache.put(mocked1);
        quickCache.put(mocked2);
        quickCache.put(mocked3);
        quickCache.get(new ItemId(98)); // Refreshing mocked1
        quickCache.put(mocked4);
        Thread.sleep(300);
        assertThat(quickCache.get(new ItemId(101))).isSameAs(mocked4);
        assertThat(quickCache.get(new ItemId(100))).isSameAs(mocked3);
        assertThat(quickCache.get(new ItemId(99))).isNull();
        assertThat(quickCache.get(new ItemId(98))).isSameAs(mocked1);
    }
}
