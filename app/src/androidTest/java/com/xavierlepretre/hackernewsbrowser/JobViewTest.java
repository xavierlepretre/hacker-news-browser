package com.xavierlepretre.hackernewsbrowser;

import android.content.Intent;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.LayoutInflater;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.OpenJobDTO;
import com.ycombinator.news.dto.UserId;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

public class JobViewTest extends AndroidTestCase
{
    private JobView view;

    @Override protected void setUp() throws Exception
    {
        super.setUp();
        view = (JobView) LayoutInflater.from(getContext()).inflate(R.layout.job, null);
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
        assertThat(view.title).isNotNull();
        assertThat(view.score).isNotNull();
        view.onDetachedFromWindow();
        assertThat(view.age).isNull();
        assertThat(view.author).isNull();
        assertThat(view.openInBrowser).isNull();
        assertThat(view.title).isNull();
        assertThat(view.score).isNull();
        view.onAttachedToWindow();
        assertThat(view.age).isNotNull();
        assertThat(view.author).isNotNull();
        assertThat(view.openInBrowser).isNotNull();
        assertThat(view.title).isNotNull();
        assertThat(view.score).isNotNull();
    }

    @SmallTest
    public void testFieldsPopulated()
    {
        view.displayJob(new JobView.DTO(
                getContext(),
                new OpenJobDTO(new ItemId(1), new UserId("fgtr"), new Date(), false, "a good title", "http://url1.com", 345, "text")));
        assertThat(view.author.getText().toString()).contains("fgtr");
        assertThat(view.age.getText()).isNotNull();
        assertThat(view.title.getText().toString()).contains("a good title");
        assertThat(view.score.getText().toString()).contains("345");
    }

    @SmallTest
    public void testFieldsHeaderPopulated()
    {
        view = (JobView) LayoutInflater.from(getContext()).inflate(R.layout.job_header, null);
        view.displayJob(new JobView.DTO(
                getContext(),
                new OpenJobDTO(new ItemId(1), new UserId("fgtr"), new Date(), false, "a good title", "http://url1.com", 345, "text")));
        assertThat(view.author.getText().toString()).contains("fgtr");
        assertThat(view.age.getText()).isNotNull();
        assertThat(view.title.getText().toString()).contains("a good title");
        assertThat(view.score.getText().toString()).contains("345");
    }

    @SmallTest
    public void testIntentContainsGivenUrl()
    {
        view.displayJob(new JobView.DTO(
                getContext(),
                new OpenJobDTO(new ItemId(1), new UserId("fgtr"), new Date(), false, "a good title", "http://url1.com", 345, "text")));
        Intent browserIntent = view.getBrowserIntent();
        //noinspection ConstantConditions
        assertThat(browserIntent.getData().toString()).contains("http://url1.com");
    }
}
