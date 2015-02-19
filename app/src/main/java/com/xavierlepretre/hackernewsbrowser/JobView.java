package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.InjectView;

public class JobView extends ItemView
{
    @InjectView(android.R.id.title) TextView title;
    @InjectView(android.R.id.text2) TextView score;

    public JobView(Context context)
    {
        super(context);
    }

    public JobView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public JobView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void displayJob(@Nullable JobViewDTO job)
    {
        displayItem(job);
        if (job != null)
        {
            title.setText(job.title);
            score.setText(job.score);
        }
        else
        {
            title.setText(android.R.string.unknownName);
            score.setText(android.R.string.unknownName);
        }
    }
}
