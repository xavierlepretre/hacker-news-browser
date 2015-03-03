package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.rx.FirebaseErrorException;
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
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;
import rx.subjects.BehaviorSubject;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HackerNewsServiceImplUnitTest
{
    private Firebase mockedFirebase478;
    private Firebase mockedFirebase479;
    private HackerNewsServiceFirebase hackerNewsServiceFirebase;
    private HackerNewsServiceImpl service;
    private QuickCache quickCache;
    private SubscriptionList subscriptionList;

    @Before public void setUp()
    {
        mockedFirebase478 = mock(Firebase.class);
        mockedFirebase479 = mock(Firebase.class);
        hackerNewsServiceFirebase = mock(HackerNewsServiceFirebase.class);
        quickCache = mock(QuickCache.class);
        service = new HackerNewsServiceImpl(hackerNewsServiceFirebase, new ApiVersion("fakeVersion"), quickCache);
        subscriptionList = new SubscriptionList();
    }

    @After public void tearDown()
    {
        subscriptionList.unsubscribe();
        service = null;
        quickCache = null;
        hackerNewsServiceFirebase = null;
        mockedFirebase479 = null;
        mockedFirebase478 = null;
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

    void makeMockedFirebaseReturn(@NonNull Firebase mockedFirebase, @NonNull final ItemDTO mocked)
    {
        final DataSnapshot mockedSnapshot = mock(DataSnapshot.class);
        when(mockedSnapshot.getValue(ItemDTO.class))
                .thenAnswer(new Answer<Object>()
                {
                    @Override public Object answer(InvocationOnMock invocation) throws Throwable
                    {
                        return mocked;
                    }
                });
        when(mockedFirebase.addValueEventListener(any(ValueEventListener.class)))
                .then(new Answer<Object>()
                {
                    @Override public Object answer(InvocationOnMock invocation) throws Throwable
                    {
                        ValueEventListener listener = (ValueEventListener) invocation.getArguments()[0];
                        listener.onDataChange(mockedSnapshot);
                        return listener;
                    }
                });
    }

    void makeMockedFirebaseReturn(@NonNull Firebase mockedFirebase, @NonNull final FirebaseError error)
    {
        when(mockedFirebase.addValueEventListener(any(ValueEventListener.class)))
                .then(new Answer<Object>()
                {
                    @Override public Object answer(InvocationOnMock invocation) throws Throwable
                    {
                        ValueEventListener listener = (ValueEventListener) invocation.getArguments()[0];
                        listener.onCancelled(error);
                        return listener;
                    }
                });
    }

    @Test public void testTopStoriesCallsFirebase() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        when(hackerNewsServiceFirebase.getTopStories(anyString())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                signal.countDown();
                return mockedFirebase478;
            }
        });
        subscriptionList.add(service.getTopStories().subscribe());
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);
    }

    @Test public void testMaxItemIdCallsFirebase() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        when(hackerNewsServiceFirebase.getMaxItemId(anyString())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                signal.countDown();
                return mockedFirebase478;
            }
        });
        subscriptionList.add(service.getMaxItem().subscribe());
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);
    }

    @Test public void testContentOneCallsFirebase() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        when(hackerNewsServiceFirebase.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo(478);
                signal.countDown();
                return mockedFirebase478;
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
        makeMockedFirebaseReturn(mockedFirebase478, mocked);
        when(hackerNewsServiceFirebase.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo(478);
                serviceSignal.countDown();
                return mockedFirebase478;
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
        when(hackerNewsServiceFirebase.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo(478);
                serviceSignal.countDown();
                makeMockedFirebaseReturn(mockedFirebase478, mock(FirebaseError.class));
                return mockedFirebase478;
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
        when(hackerNewsServiceFirebase.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo(478);
                serviceSignal.countDown();
                makeMockedFirebaseReturn(mockedFirebase478, mock(FirebaseError.class));
                return mockedFirebase478;
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
                assertThat(throwable).isExactlyInstanceOf(FirebaseErrorException.class);
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

    @Test public void testContentIterableCallsManyFirebase() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(5);
        makeMockedFirebaseReturn(mockedFirebase478, mock(ItemDTO.class));
        when(hackerNewsServiceFirebase.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo((int) signal.getCount());
                signal.countDown();
                return mockedFirebase478;
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

    @Test public void testContentIterableCallsManyWillLimitFirebase() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(5);
        when(hackerNewsServiceFirebase.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo((int) signal.getCount());
                signal.countDown();
                return mockedFirebase478; // This way we can hold it up
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

    @Test public void testContentIterableCallsManyWithConcurrentLimitFirebase() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(5);
        when(hackerNewsServiceFirebase.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo((int) signal.getCount());
                signal.countDown();
                return mockedFirebase478; // This way we can hold it up
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

    @Test public void testContentItemIdObservableCallsManyFirebase() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(5);
        makeMockedFirebaseReturn(mockedFirebase478, mock(ItemDTO.class));
        when(hackerNewsServiceFirebase.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo((int) signal.getCount());
                signal.countDown();
                return mockedFirebase478;
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

    @Test public void testContentItemIdObservableManyWillLimitFirebase() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(5);
        when(hackerNewsServiceFirebase.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo((int) signal.getCount());
                signal.countDown();
                return mockedFirebase478; // This way we can hold it up
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

    @Test public void testContentItemIdObservableManyWithConcurrentLimitFirebase() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(5);
        when(hackerNewsServiceFirebase.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo((int) signal.getCount());
                signal.countDown();
                return mockedFirebase478; // This way we can hold it up
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

    @Test public void testUserCallsFirebase() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        when(hackerNewsServiceFirebase.getUser(anyString(), anyString())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((String) invocation.getArguments()[1]).isEqualTo("someone");
                signal.countDown();
                return mockedFirebase478;
            }
        });
        subscriptionList.add(service.getUser(new UserId("someone")).subscribe());
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);
    }

    @Test public void testUpdatesCallsFirebase() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        when(hackerNewsServiceFirebase.getUpdates(anyString())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                signal.countDown();
                return mockedFirebase478;
            }
        });
        subscriptionList.add(service.getUpdates().subscribe());
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0);
    }

    @Test public void testContentIterableNotifiesLoading() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        when(hackerNewsServiceFirebase.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo(478);
                signal.countDown();
                makeMockedFirebaseReturn(mockedFirebase478, mockItemDTO(new ItemId((Integer) invocation.getArguments()[1])));
                return mockedFirebase478;
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
        when(hackerNewsServiceFirebase.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                int id = (Integer) invocation.getArguments()[1];
                assertThat(id).isIn(478, 479);
                signal.countDown();
                Thread.sleep(1000);
                ItemDTO mockedItemDTO = mockItemDTO(new ItemId(id));
                if (id == 478)
                {
                    makeMockedFirebaseReturn(mockedFirebase478, mockedItemDTO);
                    return mockedFirebase478;
                }
                else
                {
                    makeMockedFirebaseReturn(mockedFirebase479, mockedItemDTO);
                    return mockedFirebase479;
                }
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
        when(hackerNewsServiceFirebase.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                assertThat((Integer) invocation.getArguments()[1]).isEqualTo(478);
                signal.countDown();
                makeMockedFirebaseReturn(mockedFirebase478, mock(FirebaseError.class));
                return mockedFirebase478;
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
        when(hackerNewsServiceFirebase.getContent(anyString(), anyInt())).then(new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                assertThat((String) invocation.getArguments()[0]).isEqualTo("fakeVersion");
                int id = (Integer) invocation.getArguments()[1];
                assertThat(id).isIn(478, 479);
                signal.countDown();
                if (id == 478)
                {
                    makeMockedFirebaseReturn(mockedFirebase478, mock(FirebaseError.class));
                    return mockedFirebase478;
                }
                makeMockedFirebaseReturn(mockedFirebase479, mockItemDTO(new ItemId(id)));
                return mockedFirebase479;
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
