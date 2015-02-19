package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import com.ycombinator.news.dto.StoryDTO;
import java.text.NumberFormat;

class StoryViewDTO extends BaseItemViewDTO
{
    @NonNull final String title;
    @NonNull final String score;

    StoryViewDTO(@NonNull Context context, @NonNull StoryDTO storyDTO)
    {
        super(context, storyDTO);
        this.title = storyDTO.getTitle();
        this.score = context.getString(R.string.score_value, NumberFormat.getIntegerInstance().format(storyDTO.getScore()));
    }
}
