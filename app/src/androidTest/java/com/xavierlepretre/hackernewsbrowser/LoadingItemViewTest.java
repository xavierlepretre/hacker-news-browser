package com.xavierlepretre.hackernewsbrowser;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.LayoutInflater;
import com.ycombinator.news.dto.ItemId;

import static org.fest.assertions.api.Assertions.assertThat;

public class LoadingItemViewTest extends AndroidTestCase
{
    private LoadingItemView view;

    @Override protected void setUp() throws Exception
    {
        super.setUp();
        view = (LoadingItemView) LayoutInflater.from(getContext()).inflate(R.layout.loading_item, null);
    }

    @Override protected void tearDown() throws Exception
    {
        view = null;
        super.tearDown();
    }

    @SmallTest
    public void testFieldsInjectedLifecycle()
    {
        assertThat(view.title).isNotNull();
    }

    @SmallTest
    public void testFieldsPopulatedLoading()
    {
        view.displayItem(new LoadingItemView.DTO(
                getContext().getResources(),
                new ItemId(15711),
                LoadingItemView.State.LOADING));
        assertThat(view.title.getText().toString()).containsIgnoringCase("Loading");
        assertThat(view.title.getText().toString()).contains("15,711");
    }

    @SmallTest
    public void testFieldsPopulatedScheduled()
    {
        view.displayItem(new LoadingItemView.DTO(
                getContext().getResources(),
                new ItemId(15711),
                LoadingItemView.State.SCHEDULED));
        assertThat(view.title.getText().toString()).containsIgnoringCase("Waiting");
        assertThat(view.title.getText().toString()).contains("15,711");
    }

    @SmallTest
    public void testFieldsPopulatedFailed()
    {
        view.displayItem(new LoadingItemView.DTO(
                getContext().getResources(),
                new ItemId(15711),
                LoadingItemView.State.FAILED));
        assertThat(view.title.getText().toString()).containsIgnoringCase("Failed");
        assertThat(view.title.getText().toString()).contains("15,711");
    }
}
