package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import java.util.Date;

public class OpenJobDTO extends JobDTO
{
    public OpenJobDTO(
            @NonNull ItemId id,
            @NonNull UserId by,
            @NonNull Date time,
            @NonNull String title,
            @NonNull String url,
            int score,
            @NonNull String text)
    {
        super(id, by, time, title, url, score, text);
    }
}
