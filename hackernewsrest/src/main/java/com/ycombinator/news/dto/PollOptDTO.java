package com.ycombinator.news.dto;

import android.support.annotation.NonNull;

public class PollOptDTO extends ItemDTO
    implements KidItemDTO
{
    @NonNull ItemId parent;
    @NonNull String text;
    int score;

    @Override @NonNull public ItemId getParent()
    {
        return parent;
    }

    @NonNull public String getText()
    {
        return text;
    }

    public int getScore()
    {
        return score;
    }
}
