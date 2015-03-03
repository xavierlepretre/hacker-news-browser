package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import com.ycombinator.news.dto.StoryDTO;
import java.text.NumberFormat;

public class StoryView extends ItemView
{
    @InjectView(android.R.id.title) TextView title;
    @InjectView(android.R.id.text2) TextView score;
    @InjectView(android.R.id.icon1) View commentIcon;
    @InjectView(android.R.id.custom) TextView commentCount;

    public StoryView(Context context)
    {
        super(context);
    }

    public StoryView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StoryView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        commentIcon.setVisibility(VISIBLE);
        commentCount.setVisibility(VISIBLE);
    }

    public void displayStory(@NonNull DTO story)
    {
        displayItem(story);
        title.setText(story.title);
        score.setText(story.score);
        if (commentCount != null)
        {
            commentCount.setText(story.commentCount);
        }
    }

    static class DTO extends ItemView.DTO
    {
        @NonNull final String title;
        @NonNull final String score;
        @NonNull final String commentCount;

        DTO(@NonNull Context context, @NonNull StoryDTO storyDTO)
        {
            super(context, storyDTO);
            this.title = storyDTO.getTitle();
            this.score = NumberFormat.getIntegerInstance().format(storyDTO.getScore());
            int count = storyDTO.getKids().size();
            this.commentCount = String.format("%1$s (%2$s)",
                    NumberFormat.getInstance().format(count),
                    NumberFormat.getInstance().format(storyDTO.getDescendants()));
        }
    }
}
