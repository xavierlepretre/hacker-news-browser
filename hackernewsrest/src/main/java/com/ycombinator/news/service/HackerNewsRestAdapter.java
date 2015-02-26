package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavierlepretre.hackernewsrest.BuildConfig;
import com.ycombinator.news.cache.QuickCache;
import com.ycombinator.news.dto.HackerNewsDeserialisingModule;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;

public class HackerNewsRestAdapter
{
    @NonNull public static HackerNewsService createHackerNewsService()
    {
        return new HackerNewsService(
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
                .setConverter(new JacksonConverter(createHackerNewsMapper()))
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.HEADERS);
    }

    @NonNull public static ObjectMapper createHackerNewsMapper()
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
        mapper.registerModule(new HackerNewsDeserialisingModule());
        return mapper;
    }
}
