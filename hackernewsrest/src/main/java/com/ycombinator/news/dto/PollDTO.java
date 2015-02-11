package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PollDTO extends ItemDTO
    implements ParentItemDTO, SplitItemDTO
{
    @NonNull private final String title;
    @NonNull private final String text;
    private final int score;
    @NonNull private final List<ItemId> kids;
    @NonNull private final List<ItemId> parts;

    PollDTO(
            @JsonProperty(value = "id", required = true) @NonNull ItemId id,
            @JsonProperty(value = "by", required = true) @NonNull UserId by,
            @JsonProperty(value = "time", required = true) @NonNull Date time,
            @JsonProperty(value = "title", required = true) @NonNull String title,
            @JsonProperty(value = "text", required = true) @NonNull String text,
            @JsonProperty(value = "score") int score,
            @JsonProperty(value = "kids") @Nullable List<ItemId> kids,
            @JsonProperty(value = "parts") @Nullable List<ItemId> parts)
    {
        super(id, by, time);
        this.title = title;
        this.text = text;
        this.score = score;
        this.kids = Collections.unmodifiableList(kids != null ? kids : new ArrayList<ItemId>());
        this.parts = Collections.unmodifiableList(parts != null ? parts : new ArrayList<ItemId>());
        validate();
    }

    @SuppressWarnings("ConstantConditions")
    private void validate()
    {
        if (title == null || text == null)
        {
            throw new IllegalArgumentException("Missing title=" + title + ", or text=" + text);
        }
    }

    @NonNull public String getTitle()
    {
        return title;
    }

    @NonNull public String getText()
    {
        return text;
    }

    public int getScore()
    {
        return score;
    }

    @NonNull @Override public List<ItemId> getKids()
    {
        return kids;
    }

    @NonNull @Override public List<ItemId> getParts()
    {
        return parts;
    }
}