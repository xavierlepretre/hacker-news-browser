package com.ycombinator.news.service;

import android.content.Context;
import android.support.annotation.NonNull;
import com.firebase.client.Firebase;
import com.ycombinator.news.cache.QuickCache;

public class HackerNewsServiceFactory
{
    @NonNull public static HackerNewsService createHackerNewsService(@NonNull Context context)
    {
        Firebase.setAndroidContext(context);
        return new HackerNewsServiceImpl(new HackerNewsServiceFirebase(), HackerNewsConstants.version, QuickCache.Instance.get());
    }
}
