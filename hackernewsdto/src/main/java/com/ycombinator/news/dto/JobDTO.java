package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;

public class JobDTO extends ItemDTO
    implements TitledDTO, ScoredDTO, TextedDTO, WithUrlDTO
{
    @NonNull private final String title;
    @NonNull private final String url;
    private final int score;
    @NonNull private final String text;

    JobDTO(
            @JsonProperty(value = "id", required = true) @NonNull ItemId id,
            @JsonProperty(value = "by", required = true) @NonNull UserId by,
            @JsonProperty(value = "time", required = true) @NonNull Long time,
            @JsonProperty(value = "deleted") boolean deleted,
            @JsonProperty(value = "title", required = true) @NonNull String title,
            @JsonProperty(value = "url") @NonNull String url,
            @JsonProperty(value = "score") int score,
            @JsonProperty(value = "text", required = true) @NonNull String text)
    {
        super(id, by, time, deleted);
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

    @NonNull @Override public String getTitle()
    {
        return title;
    }

    @NonNull @Override public String getUrl()
    {
        return url;
    }

    @Override public int getScore()
    {
        return score;
    }

    @NonNull @Override public String getText()
    {
        return text;
    }
}
