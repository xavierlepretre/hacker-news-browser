package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StoryDTO extends ItemDTO
    implements ParentItemDTO
{
    @NonNull String title;
    @NonNull String url;
    int score;
    @Nullable List<ItemId> kids;

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
        if (kids == null)
        {
            return new ArrayList<>();
        }
        return kids;
    }
}
