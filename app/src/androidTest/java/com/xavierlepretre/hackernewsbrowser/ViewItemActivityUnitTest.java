package com.xavierlepretre.hackernewsbrowser;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.TouchUtils;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.cache.QuickCache;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.OpenCommentDTO;
import com.ycombinator.news.dto.OpenStoryDTO;
import com.ycombinator.news.dto.UserId;
import com.ycombinator.news.service.ApiVersion;
import com.ycombinator.news.service.EmptyHackerNewsServiceRetrofit;
import com.ycombinator.news.service.HackerNewsService;
import com.ycombinator.news.service.HackerNewsServiceRetrofit;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import retrofit.http.Path;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ViewItemActivityUnitTest extends ActivityUnitTestCase<ViewItemActivity>
{
    private HackerNewsServiceRetrofit serviceRetrofit;
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
                        new OpenStoryDTO(new ItemId(1), new UserId("df"), new Date(), false, "title", "url", 1, Arrays.asList(new ItemId(2)))));
        startActivity(defaultLaunchIntent, null, null);

        serviceRetrofit = new EmptyHackerNewsServiceRetrofit()
        {
            @Override public Observable<ItemDTO> getContent(@Path("version") String version, @Path("id") int id)
            {
                return Observable.just((ItemDTO) new OpenCommentDTO(new ItemId(2), new UserId("a"), new Date(), false, new ItemId(1), "comment", null));
            }
        };
        hackerNewsService = new HackerNewsService(serviceRetrofit, new ApiVersion("fake"), mock(QuickCache.class));
        getActivity().hackerNewsService = hackerNewsService;

        final StoryAsIsAdapter commentAdapter = getActivity().commentAdapter;
        assertThat(getActivity().showingItem).isEqualTo(new ItemId(999999999));
        final CountDownLatch viewReady = new CountDownLatch(1);
        runTestOnUiThread(new Runnable()
        {
            @Override public void run()
            {
                commentAdapter.add(new OpenCommentDTO(new ItemId(999999998), new UserId("a"), new Date(), false, new ItemId(999999999), "text", null));
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
