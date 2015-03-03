package com.ycombinator.news.service;

import android.content.Context;
import android.support.annotation.NonNull;
import com.xavierlepretre.hackernewsrest.BuildConfig;
import com.ycombinator.news.cache.QuickCache;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

public class HackerNewsServiceFactory
{
    @NonNull public static HackerNewsService createHackerNewsService(@SuppressWarnings("UnusedParameters") @NonNull Context ignored)
    {
        return new HackerNewsServiceImpl(
                createHackerNewsServiceRetrofit(),
                HackerNewsConstants.version,
                QuickCache.Instance.get());
    }

    @NonNull static HackerNewsServiceRetrofit createHackerNewsServiceRetrofit()
    {
        return builder().build().create(HackerNewsServiceRetrofit.class);
    }

    @NonNull static RestAdapter.Builder builder()
    {
        return new RestAdapter.Builder()
                .setEndpoint(HackerNewsConstants.PATH_PREFIX)
                .setConverter(new JacksonConverter(HackerNewsMapper.createHackerNewsMapper()))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.HEADERS);
    }
}
