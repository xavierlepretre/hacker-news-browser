package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Date;
import java.util.List;

public class OpenStoryDTO extends StoryDTO
{
    public OpenStoryDTO(
            @NonNull ItemId id,
            @NonNull UserId by,
            @NonNull Date time,
            @NonNull String title,
            @NonNull String url,
            int score,
            @Nullable List<ItemId> kids)
    {
        super(id, by, time, title, url, score, kids);
    }
}
