package com.xavierlepretre.hackernewsbrowser;

import android.database.DataSetObserver;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.OpenCommentDTO;
import com.ycombinator.news.dto.OpenItemDTO;
import com.ycombinator.news.dto.OpenJobDTO;
import com.ycombinator.news.dto.OpenStoryDTO;
import com.ycombinator.news.dto.UserId;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import rx.functions.Action1;

import static org.fest.assertions.api.Assertions.assertThat;

public class StoryAsIsAdapterTest extends AndroidTestCase
{
    private StoryAsIsAdapter adapter;

    @Override protected void setUp() throws Exception
    {
        super.setUp();
        this.adapter = new StoryAsIsAdapter(getContext());
    }

    @Override protected void tearDown() throws Exception
    {
        this.adapter = null;
        super.tearDown();
    }

    @SmallTest
    public void testSetIdsNotifiesChanged() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        adapter.registerDataSetObserver(new DataSetObserver()
        {
            @Override public void onChanged()
            {
                super.onChanged();
                signal.countDown();
            }
        });
        adapter.setIds(Arrays.asList(new ItemId(1)));
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0).as("It should have gone through without timeout");
    }

    @SmallTest
    public void testReturnsIds()
    {
        assertThat(adapter.getIds()).isEmpty();

        adapter.setIds(Arrays.asList(new ItemId(1)));
        assertThat(adapter.getIds()).isEqualTo(Arrays.asList(new ItemId(1)));

        adapter.setIds(Arrays.asList(new ItemId(1), new ItemId(2)));
        assertThat(adapter.getIds()).isEqualTo(Arrays.asList(new ItemId(1), new ItemId(2)));
    }

    @SmallTest
    public void testCountsIds()
    {
        assertThat(adapter.getCount()).isEqualTo(0);
        adapter.setIds(Arrays.asList(new ItemId(1)));
        assertThat(adapter.getCount()).isEqualTo(1);
        adapter.setIds(Arrays.asList(new ItemId(1), new ItemId(2)));
        assertThat(adapter.getCount()).isEqualTo(2);
    }

    @SmallTest
    public void testDoesNotCountDTOs()
    {
        assertThat(adapter.getCount()).isEqualTo(0);
        adapter.add(new OpenItemDTO(new ItemId(1), new UserId("a"), new Date(), false));
        assertThat(adapter.getCount()).isEqualTo(0);
        adapter.add(new OpenItemDTO(new ItemId(1), new UserId("b"), new Date(), false));
        assertThat(adapter.getCount()).isEqualTo(0);
    }

    @SmallTest
    public void testReturnsDtos()
    {
        assertThat(adapter.getReceivedDtos()).isEmpty();

        adapter.add(new OpenItemDTO(new ItemId(1), new UserId("a"), new Date(), false));
        assertThat(adapter.getReceivedDtos().size()).isEqualTo(1);
        assertThat(adapter.getReceivedDtos().get(0).getId()).isEqualTo(new ItemId(1));

        adapter.add(new OpenItemDTO(new ItemId(2), new UserId("b"), new Date(), false));
        assertThat(adapter.getReceivedDtos().size()).isEqualTo(2);
        assertThat(adapter.getReceivedDtos().get(0).getId()).isEqualTo(new ItemId(1));
        assertThat(adapter.getReceivedDtos().get(1).getId()).isEqualTo(new ItemId(2));
    }

    @SmallTest
    public void testAddDtoDoesNotNotifyChanged() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        adapter.setIds(Arrays.asList(new ItemId(1)));
        adapter.registerDataSetObserver(new DataSetObserver()
        {
            @Override public void onChanged()
            {
                super.onChanged();
                signal.countDown();
            }
        });
        adapter.add(new OpenItemDTO(new ItemId(1), new UserId("a"), new Date(), false));
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(1).as("We should have waited for the timeout interruption");
    }

    @SmallTest
    public void testAddViewDtoDoesNotNotifyChanged() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        adapter.setIds(Arrays.asList(new ItemId(1)));
        adapter.registerDataSetObserver(new DataSetObserver()
        {
            @Override public void onChanged()
            {
                super.onChanged();
                signal.countDown();
            }
        });
        adapter.add(new ItemView.DTO(getContext(), new OpenItemDTO(new ItemId(1), new UserId("a"), new Date(), false)));
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(1).as("We should have waited for the timeout interruption");
    }

    @SmallTest
    public void testAddAllDtosDoesNotNotifyChanged() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        adapter.setIds(Arrays.asList(new ItemId(1)));
        adapter.registerDataSetObserver(new DataSetObserver()
        {
            @Override public void onChanged()
            {
                super.onChanged();
                signal.countDown();
            }
        });
        adapter.addAll(Arrays.asList(
                new OpenItemDTO(new ItemId(1), new UserId("a"), new Date(), false),
                new OpenItemDTO(new ItemId(2), new UserId("b"), new Date(), false)));
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(1).as("We should have waited for the timeout interruption");
    }

    @SmallTest
    public void testAddAllViewDtosDoesNotNotifyChanged() throws InterruptedException
    {
        final CountDownLatch signal = new CountDownLatch(1);
        adapter.setIds(Arrays.asList(new ItemId(1)));
        adapter.registerDataSetObserver(new DataSetObserver()
        {
            @Override public void onChanged()
            {
                super.onChanged();
                signal.countDown();
            }
        });
        adapter.addAllViewDtos(Arrays.asList(
                new ItemView.DTO(getContext(), new OpenItemDTO(new ItemId(1), new UserId("a"), new Date(), false)),
                new ItemView.DTO(getContext(), new OpenItemDTO(new ItemId(2), new UserId("b"), new Date(), false))));
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(1).as("We should have waited for the timeout interruption");
    }

    @SmallTest
    public void testGetItemReturnsLoadingWhenHasNoDto()
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        assertThat(adapter.getItem(0)).isExactlyInstanceOf(LoadingItemView.DTO.class);
    }

    @SmallTest
    public void testGetItemReturnsDtoWhenHasIt()
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        adapter.add(new OpenItemDTO(new ItemId(1), new UserId("a"), new Date(), false));
        assertThat(adapter.getItem(0)).isNotNull();
        //noinspection ConstantConditions
        assertThat(((ItemView.DTO) adapter.getItem(0)).itemDTO.getId()).isEqualTo(new ItemId(1));
    }

    @SmallTest
    public void testGetItemIdReturnsIdEvenWhenNoDto()
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        assertThat(adapter.getItemId(0)).isEqualTo(1);
    }

    @SmallTest
    public void testGetItemIdReturnsIdWhenHasDto()
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        adapter.add(new OpenItemDTO(new ItemId(1), new UserId("a"), new Date(), false));
        assertThat(adapter.getItemId(0)).isEqualTo(1);
    }

    @SmallTest
    public void testGetViewDoesNotReInflate()
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        View view = adapter.getView(0, null, null);
        View view2 = adapter.getView(0, view, null);
        assertThat(view == view2).isTrue().as("We want the same instance to be returned");
    }

    @SmallTest
    public void testGetViewOfTypeLoadingItemWhenNoDto()
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        View view = adapter.getView(0, null, null);
        assertThat(view).isExactlyInstanceOf(LoadingItemView.class);
    }

    @SmallTest
    public void testGetViewOfTypeItemWhenItemDto()
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        adapter.add(new OpenItemDTO(new ItemId(1), new UserId("a"), new Date(), false));
        View view = adapter.getView(0, null, null);
        assertThat(view).isExactlyInstanceOf(ItemView.class);
    }

    @SmallTest
    public void testGetViewOfTypeStoryWhenStoryDto()
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        adapter.add(new OpenStoryDTO(new ItemId(1), new UserId("a"), new Date(), false, "title", "url", 32, null));
        View view = adapter.getView(0, null, null);
        assertThat(view).isExactlyInstanceOf(StoryView.class);
    }

    @SmallTest
    public void testGetViewOfTypeJobWhenJobDto()
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        adapter.add(new OpenJobDTO(new ItemId(1), new UserId("a"), new Date(), false, "title", "url", 32, "text"));
        View view = adapter.getView(0, null, null);
        assertThat(view).isExactlyInstanceOf(JobView.class);
    }

    @SmallTest
    public void testGetViewOfTypeCommentWhenCommentDto()
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        adapter.add(new OpenCommentDTO(new ItemId(1), new UserId("a"), new Date(), false, new ItemId(2), "title", null));
        View view = adapter.getView(0, null, null);
        assertThat(view).isExactlyInstanceOf(CommentView.class);
    }

    @SmallTest
    public void testSetIdsMeansScheduled()
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        assertThat(((LoadingItemView.DTO) adapter.getItem(0)).state).isEqualTo(LoadingItemView.State.SCHEDULED);
    }

    @SmallTest
    public void testAddLoadingViewDtoDoesNotOverwriteFullDto()
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        adapter.add(new JobView.DTO(getContext(), new OpenJobDTO(new ItemId(1), new UserId("a"), new Date(), false, "title", "url", 32, "text")));
        adapter.add(new LoadingItemView.DTO(getContext().getResources(), new ItemId(1), LoadingItemView.State.LOADING));
        assertThat(adapter.getItem(0)).isExactlyInstanceOf(JobView.DTO.class);
    }

    @SmallTest
    public void testNoViewDtoAndNoGetItemDoesNotRequestOnItemId() throws InterruptedException
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        final CountDownLatch signal = new CountDownLatch(1);
        adapter.getRequestedIdsObservable().subscribe(new Action1<ItemId>()
        {
            @Override public void call(ItemId itemId)
            {
                signal.countDown();
            }
        });
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(1).as("We should have waited for the timeout interruption");
    }

    @SmallTest
    public void testScheduledViewDtoAndGetItemRequestsOnItemId() throws InterruptedException
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        adapter.add(new LoadingItemView.DTO(getContext().getResources(), new ItemId(1), LoadingItemView.State.SCHEDULED));
        final CountDownLatch signal = new CountDownLatch(1);
        adapter.getRequestedIdsObservable().subscribe(new Action1<ItemId>()
        {
            @Override public void call(ItemId itemId)
            {
                signal.countDown();
            }
        });
        adapter.getItem(0);
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0).as("We should have got a request for itemId");
    }

    @SmallTest
    public void testLoadingViewDtoAndGetItemRequestsOnItemId() throws InterruptedException
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        adapter.add(new LoadingItemView.DTO(getContext().getResources(), new ItemId(1), LoadingItemView.State.LOADING));
        final CountDownLatch signal = new CountDownLatch(1);
        adapter.getRequestedIdsObservable().subscribe(new Action1<ItemId>()
        {
            @Override public void call(ItemId itemId)
            {
                signal.countDown();
            }
        });
        adapter.getItem(0);
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0).as("We should have got a request for itemId");
    }

    @SmallTest
    public void testProperDtoAndGetItemDoesNotRequestOnItemId() throws InterruptedException
    {
        adapter.setIds(Arrays.asList(new ItemId(1)));
        adapter.add(new OpenStoryDTO(new ItemId(1), new UserId("a"), new Date(), false, "title", "url", 32, null));
        final CountDownLatch signal = new CountDownLatch(1);
        adapter.getRequestedIdsObservable().subscribe(new Action1<ItemId>()
        {
            @Override public void call(ItemId itemId)
            {
                signal.countDown();
            }
        });
        adapter.getItem(0);
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(1).as("We should have received no request for itemId");
    }
}
