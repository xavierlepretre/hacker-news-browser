package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommentDTO extends ItemDTO
    implements KidItemDTO, ParentItemDTO
{
    @NonNull ItemId parent;
    @NonNull String text;
    @Nullable List<ItemId> kids;

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
        if (kids == null)
        {
            return new ArrayList<>();
        }
        return kids;
    }
}
