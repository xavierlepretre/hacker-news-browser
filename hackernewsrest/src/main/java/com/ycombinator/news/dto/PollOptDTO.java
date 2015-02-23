package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class PollOptDTO extends ItemDTO
    implements KidItemDTO, ScoredDTO, TextedDTO
{
    @NonNull private final ItemId parent;
    @NonNull private final String text;
    private final int score;

    PollOptDTO(
            @JsonProperty(value = "id", required = true) @NonNull ItemId id,
            @JsonProperty(value = "by", required = true) @NonNull UserId by,
            @JsonProperty(value = "time", required = true) @NonNull Date time,
            @JsonProperty(value = "deleted") boolean deleted,
            @JsonProperty(value = "parent", required = true) @NonNull ItemId parent,
            @JsonProperty(value = "text", required = true) @NonNull String text,
            @JsonProperty(value = "score") int score)
    {
        super(id, by, time, deleted);
        this.parent = parent;
        this.text = text;
        this.score = score;
        validate();
    }

    @SuppressWarnings("ConstantConditions")
    private void validate()
    {
        if (parent == null || text == null)
        {
            throw new IllegalArgumentException("Missing parent=" + parent + ", or text=" + text);
        }
    }

    @NonNull @Override public ItemId getParent()
    {
        return parent;
    }

    @NonNull @Override public String getText()
    {
        return text;
    }

    @Override public int getScore()
    {
        return score;
    }
}
