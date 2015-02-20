package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Date;
import java.util.List;

public class OpenCommentDTO extends CommentDTO
{
    public OpenCommentDTO(
            @NonNull ItemId id,
            @NonNull UserId by,
            @NonNull Date time,
            @NonNull ItemId parent,
            @NonNull String text,
            @Nullable List<ItemId> kids,
            boolean deleted)
    {
        super(id, by, time, parent, text, kids, deleted);
    }
}
