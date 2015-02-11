package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CommentDTO extends ItemDTO
    implements KidItemDTO, ParentItemDTO
{
    @NonNull private final ItemId parent;
    @NonNull private final String text;
    @NonNull private final List<ItemId> kids;

    CommentDTO(
            @JsonProperty(value = "id", required = true) @NonNull ItemId id,
            @JsonProperty(value = "by", required = true) @NonNull UserId by,
            @JsonProperty(value = "time", required = true) @NonNull Date time,
            @JsonProperty(value = "parent", required = true) @NonNull ItemId parent,
            @JsonProperty(value = "text", required = true) @NonNull String text,
            @JsonProperty(value = "kids") @Nullable List<ItemId> kids)
    {
        super(id, by, time);
        this.parent = parent;
        this.text = text;
        this.kids = Collections.unmodifiableList(kids != null ? kids : new ArrayList<ItemId>());
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

    @NonNull public String getText()
    {
        return text;
    }

    @NonNull @Override public List<ItemId> getKids()
    {
        return kids;
    }
}
