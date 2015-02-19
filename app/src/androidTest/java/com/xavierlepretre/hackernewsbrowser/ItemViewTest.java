package com.xavierlepretre.hackernewsbrowser;

import android.content.Intent;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.LayoutInflater;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.OpenItemDTO;
import com.ycombinator.news.dto.UserId;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

public class ItemViewTest extends AndroidTestCase
{
    private ItemView view;

    @Override protected void setUp() throws Exception
    {
        super.setUp();
        view = (ItemView) LayoutInflater.from(getContext()).inflate(R.layout.item, null);
    }

    @Override protected void tearDown() throws Exception
    {
        view = null;
        super.tearDown();
    }

    @SmallTest
    public void testFieldsInjectedLifecycle()
    {
        assertThat(view.age).isNotNull();
        assertThat(view.author).isNotNull();
        assertThat(view.openInBrowser).isNotNull();
        view.onDetachedFromWindow();
        assertThat(view.age).isNull();
        assertThat(view.author).isNull();
        assertThat(view.openInBrowser).isNull();
        view.onAttachedToWindow();
        assertThat(view.age).isNotNull();
        assertThat(view.author).isNotNull();
        assertThat(view.openInBrowser).isNotNull();
    }

    @SmallTest
    public void testFieldsPopulated()
    {
        view.displayItem(new BaseItemViewDTO(
                getContext(),
                new OpenItemDTO(new ItemId(1), new UserId("fgtr"), new Date())));
        assertThat(view.author.getText().toString()).contains("fgtr");
        assertThat(view.age.getText()).isNotNull();
    }

    @SmallTest
    public void testIntentContainsIdAndYCombinator()
    {
        view.displayItem(new BaseItemViewDTO(
                getContext(),
                new OpenItemDTO(new ItemId(1098), new UserId("fgtr"), new Date())));
        Intent browserIntent = view.getBrowserIntent();
        //noinspection ConstantConditions
        assertThat(browserIntent.getData().toString()).contains("1098");
        assertThat(browserIntent.getData().toString()).contains("ycombina");
    }
}
