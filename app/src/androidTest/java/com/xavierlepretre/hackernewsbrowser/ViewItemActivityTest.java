package com.xavierlepretre.hackernewsbrowser;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.service.HackerNewsMapper;

import static org.fest.assertions.api.Assertions.assertThat;

public class ViewItemActivityTest extends ActivityInstrumentationTestCase2<ViewItemActivity>
{
    public ViewItemActivityTest()
    {
        super(ViewItemActivity.class);
    }

    @Override protected void setUp() throws Exception
    {
        super.setUp();

        setActivityInitialTouchMode(true);
    }

    @SmallTest
    public void testLaunchIntentContainsData()
    {
        Intent intent = ViewItemActivity.createLaunchIntent(
                getInstrumentation().getContext(),
                HackerNewsMapper.createHackerNewsMapper(),
                new ItemViewDTO()
                {
                    @NonNull @Override public ItemId getItemId()
                    {
                        return new ItemId(123);
                    }
                });
        assertThat(intent.getLongExtra(ViewItemActivity.KEY_ITEM_ID, 0)).isEqualTo(123);
    }

    @SmallTest
    public void testItemIdPickedFromIntent()
    {
        Intent intent = ViewItemActivity.createLaunchIntent(
                getInstrumentation().getContext(),
                HackerNewsMapper.createHackerNewsMapper(),
                new ItemViewDTO()
                {
                    @NonNull @Override public ItemId getItemId()
                    {
                        return new ItemId(123);
                    }
                });
        setActivityIntent(intent);
        ViewItemActivity activity = getActivity();
        assertThat(activity.showingItem).isEqualTo(new ItemId(123));
    }
}
