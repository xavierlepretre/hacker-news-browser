package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StoryDTO extends ItemDTO
    implements ParentItemDTO, TitledDTO, ScoredDTO, WithUrlDTO, TextedDTO, WithDescendantsDTO
{
    @NonNull private final String title;
    @NonNull private final String url;
    private final int score;
    @NonNull private final String text;
    @NonNull private final List<ItemId> kids;
    private final int descendants;

    StoryDTO(
            @JsonProperty(value = "id", required = true) @NonNull ItemId id,
            @JsonProperty(value = "by", required = true) @NonNull UserId by,
            @JsonProperty(value = "time", required = true) @NonNull Long time,
            @JsonProperty(value = "deleted") boolean deleted,
            @JsonProperty(value = "title") @NonNull String title,
            @JsonProperty(value = "url") @NonNull String url,
            @JsonProperty(value = "score") int score,
            @JsonProperty(value = "text") @Nullable String text,
            @JsonProperty(value = "kids") @Nullable List<ItemId> kids,
            @JsonProperty(value = "descendants", required = true) int descendants)
    {
        super(id, by, time, deleted);
        this.title = title;
        this.url = url;
        this.score = score;
        this.text = text == null ? "" : text;
        this.kids = Collections.unmodifiableList(kids != null ? kids : new ArrayList<ItemId>());
        this.descendants = descendants;
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

    @NonNull public String getText()
    {
        return text;
    }

    @NonNull @Override public List<ItemId> getKids()
    {
        return kids;
    }

    @Override public int getDescendants()
    {
        return descendants;
    }
}
