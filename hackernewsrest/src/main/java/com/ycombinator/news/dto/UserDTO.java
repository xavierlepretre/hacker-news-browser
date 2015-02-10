package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserDTO
{
    @NonNull UserId id;
    int karma;
    int delay;
    @NonNull Date created;
    @NonNull String about;
    @Nullable List<ItemId> submitted;

    @NonNull public UserId getId()
    {
        return id;
    }

    public int getKarma()
    {
        return karma;
    }

    public int getDelay()
    {
        return delay;
    }

    @NonNull public Date getCreated()
    {
        return created;
    }

    @NonNull public String getAbout()
    {
        return about;
    }

    @NonNull public List<ItemId> getSubmitted()
    {
        if (submitted == null)
        {
            return new ArrayList<>();
        }
        return submitted;
    }
}
