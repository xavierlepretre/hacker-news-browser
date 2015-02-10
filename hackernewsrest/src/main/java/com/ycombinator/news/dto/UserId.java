package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonCreator;

public class UserId
{
    @NonNull public final String id;

    @JsonCreator public UserId(@NonNull String id)
    {
        this.id = id;
    }

    @Override public int hashCode()
    {
        return id.hashCode();
    }

    @Override public boolean equals(@Nullable Object other)
    {
        return other instanceof UserId
                && ((UserId) other).id.equals(id);
    }
}
