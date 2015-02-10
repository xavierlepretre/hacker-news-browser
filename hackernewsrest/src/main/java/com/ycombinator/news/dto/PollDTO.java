package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PollDTO extends ItemDTO
    implements ParentItemDTO, SplitItemDTO
{
    @NonNull String title;
    @NonNull String text;
    int score;
    @Nullable List<ItemId> kids;
    @Nullable List<ItemId> parts;

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
        if (kids == null)
        {
            return new ArrayList<>();
        }
        return kids;
    }

    @NonNull @Override public List<ItemId> getParts()
    {
        if (parts == null)
        {
            return new ArrayList<>();
        }
        return parts;
    }
}
