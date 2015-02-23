package com.xavierlepretre.hackernewsbrowser;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.LayoutInflater;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.OpenCommentDTO;
import com.ycombinator.news.dto.UserId;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

public class CommentViewTest extends AndroidTestCase
{
    private CommentView view;

    @Override protected void setUp() throws Exception
    {
        super.setUp();
        view = (CommentView) LayoutInflater.from(getContext()).inflate(R.layout.comment, null);
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
        assertThat(view.openInBrowser).isNull();
        assertThat(view.text).isNotNull();
        view.onDetachedFromWindow();
        assertThat(view.age).isNull();
        assertThat(view.author).isNull();
        assertThat(view.openInBrowser).isNull();
        assertThat(view.text).isNull();
        view.onAttachedToWindow();
        assertThat(view.age).isNotNull();
        assertThat(view.author).isNotNull();
        assertThat(view.openInBrowser).isNull();
        assertThat(view.text).isNotNull();
    }

    @SmallTest
    public void testFieldsPopulated()
    {
        view.displayComment(new CommentView.DTO(
                getContext(),
                new OpenCommentDTO(new ItemId(1), new UserId("fgtr"), new Date(), false, new ItemId(2), "a good title", null),
                0,
                false));
        assertThat(view.author.getText().toString()).contains("fgtr");
        assertThat(view.age.getText()).isNotNull();
        assertThat(view.text.getText().toString()).contains("a good title");
        assertThat(view.getPaddingLeft()).isEqualTo(view.getPaddingRight());
    }
}
