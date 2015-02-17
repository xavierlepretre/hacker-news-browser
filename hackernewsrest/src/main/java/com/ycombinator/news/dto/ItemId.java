package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class ItemId
{
    @NonNull public final Integer id;

    @JsonCreator public ItemId(int id)
    {
        this.id = id;
    }

    @Override public int hashCode()
    {
        return id.hashCode();
    }

    @Override public boolean equals(@Nullable Object other)
    {
        return other instanceof ItemId
                && ((ItemId) other).id.equals(id);
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonValue Integer serialise()
    {
        return id;
    }
}
