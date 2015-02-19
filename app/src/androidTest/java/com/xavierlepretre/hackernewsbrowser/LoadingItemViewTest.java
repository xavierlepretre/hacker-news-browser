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
    public void testFieldsPopulated()
    {
        view.displayItem(new LoadingItemViewDTO(
                getContext().getResources(),
                new ItemId(15711)));
        assertThat(view.title.getText().toString()).contains("15,711");
    }
}