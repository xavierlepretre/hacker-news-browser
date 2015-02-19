package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.InjectView;

public class StoryView extends ItemView
{
    @InjectView(android.R.id.title) TextView title;
    @InjectView(android.R.id.text2) TextView score;

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

    public void displayStory(@NonNull StoryViewDTO story)
    {
        displayItem(story);
        title.setText(story.title);
        score.setText(story.score);
    }
}
