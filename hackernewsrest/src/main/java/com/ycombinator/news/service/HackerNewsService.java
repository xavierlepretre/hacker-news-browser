package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.UpdateDTO;
import com.ycombinator.news.dto.UserDTO;
import com.ycombinator.news.dto.UserId;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;

public class HackerNewsService
{
    public final static int PARALLEL_COUNT_STORY = 3;

    @NonNull private final HackerNewsServiceRetrofit service;
    @NonNull private final ApiVersion version;

    public HackerNewsService(@NonNull HackerNewsServiceRetrofit service,
            @NonNull ApiVersion version)
    {
        this.service = service;
        this.version = version;
    }

    @NonNull public Observable<List<ItemId>> getTopStories()
    {
        return service.getTopStories(version.id);
    }

    @NonNull public Observable<ItemId> getMaxItem()
    {
        return service.getMaxItemId(version.id);
    }

    @NonNull public Observable<ItemDTO> getContent(@NonNull ItemId itemId)
    {
        return service.getContent(version.id, itemId.id);
    }

    @NonNull public Observable<ItemDTO> getContent(@NonNull Iterable<? extends ItemId> itemIds)
    {
        return getContent(itemIds, PARALLEL_COUNT_STORY);
    }

    @NonNull public Observable<ItemDTO> getContent(@NonNull Iterable<? extends ItemId> itemIds, int maxConcurrent)
    {
        return Observable.merge(
            Observable.from(itemIds)
                .map(new Func1<ItemId, Observable<ItemDTO>>()
                {
                    @Override public Observable<ItemDTO> call(ItemId itemId)
                    {
                        return getContent(itemId);
                    }
                }),
                maxConcurrent);
    }

    @NonNull public Observable<UserDTO> getUser(@NonNull UserId userId)
    {
        return service.getUser(version.id, userId.id);
    }

    @NonNull public Observable<UpdateDTO> getUpdates()
    {
        return service.getUpdates(version.id);
    }
}

