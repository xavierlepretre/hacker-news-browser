package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class JobDTO extends ItemDTO
{
    @NonNull private final String title;
    @NonNull private final String url;
    private final int score;
    @NonNull private final String text;

    JobDTO(
            @JsonProperty(value = "id", required = true) @NonNull ItemId id,
            @JsonProperty(value = "by", required = true) @NonNull UserId by,
            @JsonProperty(value = "time", required = true) @NonNull Date time,
            @JsonProperty(value = "title", required = true) @NonNull String title,
            @JsonProperty(value = "url") @NonNull String url,
            @JsonProperty(value = "score") int score,
            @JsonProperty(value = "text", required = true) @NonNull String text)
    {
        super(id, by, time);
        this.title = title;
        this.url = url;
        this.score = score;
        this.text = text;
        validate();
    }

    @SuppressWarnings("ConstantConditions")
    private void validate()
    {
        if (title == null || url == null || text == null)
        {
            throw new IllegalArgumentException("Missing title=" + title + ", or url=" + url + ", or text=" + text);
        }
    }

    @NonNull public String getTitle()
    {
        return title;
    }

    @NonNull public String getUrl()
    {
        return url;
    }

    public int getScore()
    {
        return score;
    }

    @NonNull public String getText()
    {
        return text;
    }
}
