package com.xavierlepretre.hackernewsbrowser;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.OpenCommentDTO;
import com.ycombinator.news.dto.OpenStoryDTO;
import com.ycombinator.news.dto.UserId;
import com.ycombinator.news.service.HackerNewsService;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Subscription;
import rx.functions.Action1;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ViewItemActivityUnitTest extends ActivityUnitTestCase<ViewItemActivity>
{
    private HackerNewsService hackerNewsService;
    private Intent defaultLaunchIntent;

    public ViewItemActivityUnitTest()
    {
        super(ViewItemActivity.class);
    }

    @MediumTest
    public void testBackToParentClickedNotifiesObservable() throws Throwable
    {
        defaultLaunchIntent = ViewItemActivity.createLaunchIntent(
                getInstrumentation().getContext(),
                new ObjectMapper(),
                new ItemView.DTO(
                        getInstrumentation().getContext(),
                        new OpenStoryDTO(new ItemId(1), new UserId("df"), 123L, false, "title", "url", 1, "no text", Arrays.asList(new ItemId(2)), 34)));
        startActivity(defaultLaunchIntent, null, null);

        hackerNewsService = mock(HackerNewsService.class);
        when(hackerNewsService.getContent(any(ItemId.class)))
                .then(new Answer<Object>()
                {
                    @Override public Object answer(InvocationOnMock invocation) throws Throwable
                    {
                        return new OpenCommentDTO(new ItemId(2), new UserId("a"), 123L, false, new ItemId(1), "comment", null);
                    }
                });
        getActivity().hackerNewsService = hackerNewsService;

        final StoryAsIsAdapter commentAdapter = getActivity().commentAdapter;
        assertThat(getActivity().showingItem).isEqualTo(new ItemId(999999999));
        final CountDownLatch viewReady = new CountDownLatch(1);
        runTestOnUiThread(new Runnable()
        {
            @Override public void run()
            {
                commentAdapter.add(new OpenCommentDTO(new ItemId(999999998), new UserId("a"), 123L, false, new ItemId(999999999), "text", null));
                commentAdapter.notifyDataSetChanged();
                viewReady.countDown();
                System.out.println("Counted down view ready");
            }
        });
        viewReady.await(10, TimeUnit.SECONDS);
        assertThat(commentAdapter.getIds().size()).isEqualTo(1);
        final CountDownLatch signal = new CountDownLatch(1);
        Subscription backToParentSubscription = commentAdapter.getBackToParentObservable().subscribe(new Action1<StoryAsIsAdapter.PositionedItemId>()
        {
            @Override public void call(StoryAsIsAdapter.PositionedItemId itemId)
            {
                signal.countDown();
            }
        });
        View backToParentButton = commentAdapter.getView(0, null, null).findViewById(android.R.id.button2);
        assertThat(backToParentButton).isNotNull();
        View otherView = commentAdapter.getView(0, null, null).findViewById(android.R.id.content);
        View toolbar = getActivity().findViewById(R.id.toolbar);
        ViewAsserts.assertOnScreen(getActivity().getWindow().getDecorView(), backToParentButton);
        TouchUtils.clickView(this, toolbar);
        TouchUtils.clickView(this, otherView);
        TouchUtils.clickView(this, backToParentButton);
        signal.await(1, TimeUnit.SECONDS);
        assertThat(signal.getCount()).isEqualTo(0).as("The callback should have counted down");
        backToParentSubscription.unsubscribe();
    }

}
