package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Date;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = ItemDTO.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OpenCommentDTO.class, name = "comment"),
})
public class OpenCommentDTO extends CommentDTO
{
    public OpenCommentDTO(
            @NonNull ItemId id,
            @NonNull UserId by,
            @NonNull Date time,
            boolean deleted,
            @NonNull ItemId parent,
            @NonNull String text,
            @Nullable List<ItemId> kids)
    {
        super(id, by, time, deleted, parent, text, kids);
    }
}
