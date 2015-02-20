package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CommentDTO extends ItemDTO
    implements KidItemDTO, ParentItemDTO, TextedDTO, DeletableDTO
{
    @NonNull private final ItemId parent;
    @NonNull private final String text;
    @NonNull private final List<ItemId> kids;
    private final boolean deleted;

    CommentDTO(
            @JsonProperty(value = "id", required = true) @NonNull ItemId id,
            @JsonProperty(value = "by", required = true) @Nullable UserId by,
            @JsonProperty(value = "time", required = true) @NonNull Date time,
            @JsonProperty(value = "parent", required = true) @NonNull ItemId parent,
            @JsonProperty(value = "text", required = true) @Nullable String text,
            @JsonProperty(value = "kids") @Nullable List<ItemId> kids,
            @JsonProperty(value = "deleted", required = false) boolean deleted)
    {
        super(id,
                by != null ? by : new UserId("deleted"),
                time);
        this.parent = parent;
        this.text = text != null ? text : "deleted";
        this.kids = Collections.unmodifiableList(kids != null ? kids : new ArrayList<ItemId>());
        this.deleted = deleted;
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

    @NonNull @Override public List<ItemId> getKids()
    {
        return kids;
    }

    @Override public boolean isDeleted()
    {
        return deleted;
    }
}
