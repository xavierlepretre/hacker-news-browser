package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.ycombinator.news.cache.QuickCache;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.UserId;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Observable;
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;
import rx.subjects.BehaviorSubject;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HackerNewsServiceImplUnitTest
{
    private HackerNewsServiceRetrofit serviceRetrofit;
    private HackerNewsServiceImpl service;
    private QuickCache quickCache;
    private SubscriptionList subscriptionList;

    @Before public void setUp()
    {
        serviceRetrofit = mock(HackerNewsServiceRetrofit.class);
        quickCache = mock(QuickCache.class);
        service = new HackerNewsServiceImpl(serviceRetrofit, new ApiVersion("fakeVersion"), quickCache);
        subscriptionList = new SubscriptionList();
    }

    @After public void tearDown()
    {
        subscriptionList.unsubscribe();
        service = null;
        quickCache = null;
        serviceRetrofit = null;
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

    @Test public void testTopStoriesCallsRetrofit() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        when(serviceRetrofit.getTopStories(anyString())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                signal.countDown();
                return Observable.empty();
            }
        });
        subscriptionList.add(service.getTopStories().subscribe());
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);
    }

    @Test public void testMaxItemIdCallsRetrofit() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        when(serviceRetrofit.getMaxItemId(anyString())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                signal.countDown();
                return Observable.empty();
            }
        });
        subscriptionList.add(service.getMaxItem().subscribe());
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);
    }

    @Test public void testContentOneCallsRetrofit() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        when(serviceRetrofit.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo(478);
                signal.countDown();
                return Observable.empty();
            }
        });
        subscriptionList.add(service.getContent(new ItemId(478)).subscribe());
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);
    }

    @Test public void testContentOnePutsIntoCache() throws InterruptedException
    {
        final ItemDTO mocked = mockItemDTO(new ItemId(478));

        final CountDownLatch cacheSignal = new CountDownLatch(1);
        doAnswer(new Answer()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                cacheSignal.countDown();
                return null;
            }
        }).when(quickCache).put(mocked);
        final CountDownLatch serviceSignal = new CountDownLatch(1);
        when(serviceRetrofit.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo(478);
                serviceSignal.countDown();
                return Observable.just(mocked);
            }
        });
        subscriptionList.add(service.getContent(new ItemId(478)).subscribe());
        serviceSignal.await(1, TimeUnit.SECONDS);
        assertThat(serviceSignal.getCount()).isEqualTo(0);
        cacheSignal.await(1, TimeUnit.SECONDS);
        assertThat(cacheSignal.getCount()).isEqualTo(0);
    }

    @Test public void testContentOneTakesFromCacheIfFailed() throws InterruptedException
    {
        final ItemDTO mocked = mockItemDTO(new ItemId(478));
        final CountDownLatch cacheSignal = new CountDownLatch(1);
        when(quickCache.get(new ItemId(478))).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                cacheSignal.countDown();
                return mocked;
            }
        });

        final CountDownLatch serviceSignal = new CountDownLatch(1);
        when(serviceRetrofit.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo(478);
                serviceSignal.countDown();
                return Observable.just(new Throwable());
            }
        });
        Iterator<ItemDTO> iterator = service.getContent(new ItemId(478)).toBlocking().getIterator();
        serviceSignal.await(1, TimeUnit.SECONDS);
        assertThat(serviceSignal.getCount()).isEqualTo(0);

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo(mocked);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test public void testContentOnePassesErrorIfNoCacheWhenFailed() throws InterruptedException
    {
        final CountDownLatch cacheSignal = new CountDownLatch(1);
        when(quickCache.get(new ItemId(478))).then(new Answer()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                cacheSignal.countDown();
                return null;
            }
        });

        final CountDownLatch serviceSignal = new CountDownLatch(1);
        when(serviceRetrofit.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo(478);
                serviceSignal.countDown();
                return Observable.error(new IllegalArgumentException());
            }
        });
        final CountDownLatch subscriberSignal = new CountDownLatch(2);
        subscriptionList.add(service.getContent(new ItemId(478)).subscribe(new Action1<ItemDTO>()
        {
            @Override public void call(ItemDTO itemDTO)
            {
            }
        }, new Action1<Throwable>()
        {
            @Override public void call(Throwable throwable)
            {
                subscriberSignal.countDown();
                assertThat(throwable).isExactlyInstanceOf(IllegalArgumentException.class);
                subscriberSignal.countDown();
            }
        }));
        subscriberSignal.await(1, TimeUnit.SECONDS);
        assertThat(subscriberSignal.getCount()).isEqualTo(0);
        serviceSignal.await(1, TimeUnit.SECONDS);
        assertThat(serviceSignal.getCount()).isEqualTo(0);
        cacheSignal.await(1, TimeUnit.SECONDS);
        assertThat(cacheSignal.getCount()).isEqualTo(0);
    }

    @Test public void testContentIterableCallsManyRetrofit() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(5);
        when(serviceRetrofit.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo((int) signal.getCount());
                signal.countDown();
                return Observable.empty();
            }
        });
        subscriptionList.add(service.getContent(Arrays.asList(
                new ItemId(5),
                new ItemId(4),
                new ItemId(3),
                new ItemId(2),
                new ItemId(1))).subscribe());
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);
    }

    @Test public void testContentIterableCallsManyWillLimitRetrofit() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(5);
        when(serviceRetrofit.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo((int) signal.getCount());
                signal.countDown();
                return Observable.never(); // This way we can hold it up
            }
        });
        subscriptionList.add(service.getContent(Arrays.asList(
                        new ItemId(5),
                        new ItemId(4),
                        new ItemId(3),
                        new ItemId(2),
                        new ItemId(1)),
                3).subscribe());
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(5 - HackerNewsServiceImpl.PARALLEL_COUNT_CONTENT);
    }

    @Test public void testContentIterableCallsManyWithConcurrentLimitRetrofit() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(5);
        when(serviceRetrofit.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo((int) signal.getCount());
                signal.countDown();
                return Observable.never(); // This way we can hold it up
            }
        });
        subscriptionList.add(service.getContent(Arrays.asList(
                        new ItemId(5),
                        new ItemId(4),
                        new ItemId(3),
                        new ItemId(2),
                        new ItemId(1)),
                2).subscribe());
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(3);
    }

    @Test public void testContentItemIdObservableCallsManyRetrofit() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(5);
        when(serviceRetrofit.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo((int) signal.getCount());
                signal.countDown();
                return Observable.empty();
            }
        });
        BehaviorSubject<ItemId> subject = BehaviorSubject.create();
        subscriptionList.add(service.getContentFromIds(subject.asObservable()).subscribe());
        subject.onNext(new ItemId(5));
        signal.await(100, TimeUnit.MILLISECONDS);
        assertThat(signal.getCount()).isEqualTo(4);
        subject.onNext(new ItemId(4));
        signal.await(100, TimeUnit.MILLISECONDS);
        assertThat(signal.getCount()).isEqualTo(3);
        subject.onNext(new ItemId(3));
        signal.await(100, TimeUnit.MILLISECONDS);
        assertThat(signal.getCount()).isEqualTo(2);
        subject.onNext(new ItemId(2));
        signal.await(100, TimeUnit.MILLISECONDS);
        assertThat(signal.getCount()).isEqualTo(1);
        subject.onNext(new ItemId(1));
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);
    }

    @Test public void testContentItemIdObservableManyWillLimitRetrofit() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(5);
        when(serviceRetrofit.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo((int) signal.getCount());
                signal.countDown();
                return Observable.never(); // This way we can hold it up
            }
        });
        BehaviorSubject<ItemId> subject = BehaviorSubject.create();
        subscriptionList.add(service.getContentFromIds(subject.asObservable()).subscribe());
        subject.onNext(new ItemId(5));
        subject.onNext(new ItemId(4));
        subject.onNext(new ItemId(3));
        subject.onNext(new ItemId(2));
        subject.onNext(new ItemId(1));
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(5 - HackerNewsServiceImpl.PARALLEL_COUNT_CONTENT);
    }

    @Test public void testContentItemIdObservableManyWithConcurrentLimitRetrofit() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(5);
        when(serviceRetrofit.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo((int) signal.getCount());
                signal.countDown();
                return Observable.never(); // This way we can hold it up
            }
        });
        BehaviorSubject<ItemId> subject = BehaviorSubject.create();
        subscriptionList.add(service.getContentFromIds(subject.asObservable(), 2).subscribe());
        subject.onNext(new ItemId(5));
        subject.onNext(new ItemId(4));
        subject.onNext(new ItemId(3));
        subject.onNext(new ItemId(2));
        subject.onNext(new ItemId(1));
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(3);
    }

    @Test public void testUserCallsRetrofit() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        when(serviceRetrofit.getUser(anyString(), anyString())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((String) invocation.getArguments()[1]).isEqualTo("someone");
                signal.countDown();
                return Observable.empty();
            }
        });
        subscriptionList.add(service.getUser(new UserId("someone")).subscribe());
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);
    }

    @Test public void testUpdatesCallsRetrofit() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        when(serviceRetrofit.getUpdates(anyString())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                signal.countDown();
                return Observable.empty();
            }
        });
        subscriptionList.add(service.getUpdates().subscribe());
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);
    }

    @Test public void testContentIterableNotifiesLoading() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        when(serviceRetrofit.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo(478);
                signal.countDown();
                return Observable.just(mockItemDTO(new ItemId((Integer) invocation.getArguments()[1])));
            }
        });
        Iterator<LoadingItemDTO> iterator = service.getContent(Arrays.asList(new ItemId(478))).toBlocking().getIterator();
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);

        assertThat(iterator.hasNext()).isTrue();
        LoadingItemDTO dto = iterator.next();
        assertThat(dto).isExactlyInstanceOf(LoadingItemStartedDTO.class);
        assertThat(dto.getItemId()).isEqualTo(new ItemId(478));

        assertThat(iterator.hasNext()).isTrue();
        dto = iterator.next();
        assertThat(dto).isExactlyInstanceOf(LoadingItemFinishedDTO.class);
        assertThat(dto.getItemId()).isEqualTo(new ItemId(478));

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test public void testContentIterable2NotifiesLoading() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(2);
        when(serviceRetrofit.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isIn(478, 479);
                signal.countDown();
                Thread.sleep(1000);
                return Observable.just(mockItemDTO(new ItemId((Integer) invocation.getArguments()[1])));
            }
        });
        Iterator<LoadingItemDTO> iterator = service.getContent(Arrays.asList(new ItemId(478), new ItemId(479))).toBlocking().getIterator();
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);

        assertThat(iterator.hasNext()).isTrue();
        LoadingItemDTO dto = iterator.next();
        assertThat(dto).isExactlyInstanceOf(LoadingItemStartedDTO.class);
        assertThat(dto.getItemId()).isEqualTo(new ItemId(478));

        assertThat(iterator.hasNext()).isTrue();
        dto = iterator.next();
        assertThat(dto).isExactlyInstanceOf(LoadingItemFinishedDTO.class);
        assertThat(dto.getItemId()).isEqualTo(new ItemId(478));

        assertThat(iterator.hasNext()).isTrue();
        dto = iterator.next();
        assertThat(dto).isExactlyInstanceOf(LoadingItemStartedDTO.class);
        assertThat(dto.getItemId()).isEqualTo(new ItemId(479));

        assertThat(iterator.hasNext()).isTrue();
        dto = iterator.next();
        assertThat(dto).isExactlyInstanceOf(LoadingItemFinishedDTO.class);
        assertThat(dto.getItemId()).isEqualTo(new ItemId(479));

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test public void testContentIterableCanNotifyFailed() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        when(serviceRetrofit.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo(478);
                signal.countDown();
                return Observable.error(new Throwable());
            }
        });

        Iterator<LoadingItemDTO> iterator = service.getContent(Arrays.asList(new ItemId(478))).toBlocking().getIterator();
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);

        assertThat(iterator.hasNext()).isTrue();
        LoadingItemDTO dto = iterator.next();
        assertThat(dto).isExactlyInstanceOf(LoadingItemStartedDTO.class);
        assertThat(dto.getItemId()).isEqualTo(new ItemId(478));

        assertThat(iterator.hasNext()).isTrue();
        dto = iterator.next();
        assertThat(dto).isExactlyInstanceOf(LoadingItemFailedDTO.class);
        assertThat(dto.getItemId()).isEqualTo(new ItemId(478));

        assertThat(iterator.hasNext()).isFalse();
    }

    @Test public void testContentIterableCanNotifyFailedWithoutStoppingSubsequent() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(2);
        when(serviceRetrofit.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isIn(478, 479);
                signal.countDown();
                if (invocation.getArguments()[1].equals(478))
                {
                    return Observable.error(new Throwable());
                }
                return Observable.just(mockItemDTO(new ItemId((Integer) invocation.getArguments()[1])));
            }
        });

        Iterator<LoadingItemDTO> iterator = service.getContent(Arrays.asList(new ItemId(478), new ItemId(479))).toBlocking().getIterator();
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);

        assertThat(iterator.hasNext()).isTrue();
        LoadingItemDTO dto = iterator.next();
        assertThat(dto).isExactlyInstanceOf(LoadingItemStartedDTO.class);
        assertThat(dto.getItemId()).isEqualTo(new ItemId(478));

        assertThat(iterator.hasNext()).isTrue();
        dto = iterator.next();
        assertThat(dto).isExactlyInstanceOf(LoadingItemFailedDTO.class);
        assertThat(dto.getItemId()).isEqualTo(new ItemId(478));

        assertThat(iterator.hasNext()).isTrue();
        dto = iterator.next();
        assertThat(dto).isExactlyInstanceOf(LoadingItemStartedDTO.class);
        assertThat(dto.getItemId()).isEqualTo(new ItemId(479));

        assertThat(iterator.hasNext()).isTrue();
        dto = iterator.next();
        assertThat(dto).isExactlyInstanceOf(LoadingItemFinishedDTO.class);
        assertThat(dto.getItemId()).isEqualTo(new ItemId(479));

        assertThat(iterator.hasNext()).isFalse();
    }
}
