package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ApiVersion
{
    @NonNull public final String id;

    public ApiVersion(@NonNull String id)
    {
        this.id = id;
    }

    @Override public int hashCode()
    {
        return id.hashCode();
    }

    @Override public boolean equals(@Nullable Object other)
    {
        return other instanceof ApiVersion
                && ((ApiVersion) other).id.equals(id);
    }
}
