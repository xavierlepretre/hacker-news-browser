package com.xavierlepretre.hackernewsbrowser;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.ListView;

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
        setActivityInitialTouchMode(true);
    }

    @SmallTest
    public void testPreconditions()
    {
        assertThat(getActivity().listView).isNotNull();
        ViewAsserts.assertOnScreen(getActivity().getWindow().getDecorView(), getActivity().listView);
        assertThat(getActivity().listView.getAdapter()).isExactlyInstanceOf(StoryAsIsAdapter.class);
        assertThat(getActivity().topStoriesSubscription).isNotNull();
    }

    @LargeTest
    public void testElementsPresent()
    {
        ListView listView = (ListView) getActivity().findViewById(android.R.id.list);
        ViewAsserts.assertOnScreen(getActivity().getWindow().getDecorView(), listView);
    }
}
