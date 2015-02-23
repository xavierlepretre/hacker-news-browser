package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Optional;
import com.ycombinator.news.dto.JobDTO;
import java.text.NumberFormat;

public class JobView extends ItemView
{
    @InjectView(android.R.id.title) TextView title;
    @InjectView(android.R.id.text2) TextView score;
    @InjectView(android.R.id.content) @Optional TextView text;

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

    public void displayJob(@NonNull DTO job)
    {
        displayItem(job);
        title.setText(job.title);
        score.setText(job.score);
        if (text != null)
        {
            text.setText(job.text);
        }
    }

    static class DTO extends ItemView.DTO
    {
        @NonNull final String title;
        @NonNull final String score;
        @NonNull final String text;

        DTO(@NonNull Context context, @NonNull JobDTO jobDTO)
        {
            super(context, jobDTO);
            this.title = jobDTO.getTitle();
            this.score = NumberFormat.getIntegerInstance().format(jobDTO.getScore());
            this.text = jobDTO.getText();
        }
    }
}
