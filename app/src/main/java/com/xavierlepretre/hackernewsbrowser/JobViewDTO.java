package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import com.ycombinator.news.dto.JobDTO;
import java.text.NumberFormat;

class JobViewDTO extends BaseItemViewDTO
{
    @NonNull final String title;
    @NonNull final String score;

    JobViewDTO(@NonNull Context context, @NonNull JobDTO jobDTO)
    {
        super(context, jobDTO);
        this.title = jobDTO.getTitle();
        this.score = context.getString(R.string.score_value, NumberFormat.getIntegerInstance().format(jobDTO.getScore()));
    }
}
