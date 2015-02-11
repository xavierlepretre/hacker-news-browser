package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class StoryDTO extends ItemDTO
    implements ParentItemDTO
{
    @NonNull private final String title;
    @NonNull private final String url;
    private final int score;
    @NonNull private final List<ItemId> kids;

    StoryDTO(
            @JsonProperty(value = "id", required = true) @NonNull ItemId id,
            @JsonProperty(value = "by", required = true) @NonNull UserId by,
            @JsonProperty(value = "time", required = true) @NonNull Date time,
            @JsonProperty(value = "title") @NonNull String title,
            @JsonProperty(value = "url") @NonNull String url,
            @JsonProperty(value = "score") int score,
            @JsonProperty(value = "kids") @Nullable List<ItemId> kids)
    {
        super(id, by, time);
        this.title = title;
        this.url = url;
        this.score = score;
        this.kids = Collections.unmodifiableList(kids != null ? kids : new ArrayList<ItemId>());
        validate();
    }

    @SuppressWarnings("ConstantConditions")
    private void validate()
    {
        if (title == null || url == null)
        {
            throw new IllegalArgumentException("Missing title=" + title + ", or url=" + url);
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

    @NonNull @Override public List<ItemId> getKids()
    {
        return kids;
    }
}
