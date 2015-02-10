package com.ycombinator.news.service;

import android.support.annotation.NonNull;

public class ApiVersion
{
    @NonNull public final String id;

    public ApiVersion(@NonNull String id)
    {
        this.id = id;
    }
}
