package com.xavierlepretre.hackernewsbrowser;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.SmallTest;

import static org.fest.assertions.api.Assertions.assertThat;

public class TopStoriesActivityTest extends ActivityInstrumentationTestCase2<TopStoriesActivity>
{
    public TopStoriesActivityTest()
    {
        super(TopStoriesActivity.class);
    }

    @Override protected void setUp() throws Exception
    {
        super.setUp();
    }

    @SmallTest
    public void testPreconditions()
    {
        assertThat(getActivity().listView).isNotNull();
        ViewAsserts.assertOnScreen(getActivity().getWindow().getDecorView(), getActivity().listView);
        assertThat(getActivity().listView.getAdapter()).isExactlyInstanceOf(StoryAsIsAdapter.class);
        assertThat(getActivity().topStoriesSubscription).isNotNull();
    }
}
