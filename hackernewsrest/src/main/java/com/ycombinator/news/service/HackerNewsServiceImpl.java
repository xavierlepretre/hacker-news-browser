package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.ycombinator.news.cache.QuickCache;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.UpdateDTO;
import com.ycombinator.news.dto.UserDTO;
import com.ycombinator.news.dto.UserId;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class HackerNewsServiceImpl extends HackerNewsThrottledService
{
    @NonNull private final HackerNewsServiceRetrofit service;
    @NonNull private final ApiVersion version;
    @NonNull private final QuickCache quickCache;

    public HackerNewsServiceImpl(@NonNull HackerNewsServiceRetrofit service,
            @NonNull ApiVersion version,
            @NonNull QuickCache quickCache)
    {
        this.service = service;
        this.version = version;
        this.quickCache = quickCache;
    }

    @NonNull @Override public Observable<List<ItemId>> getTopStories()
    {
        return service.getTopStories(version.id);
    }

    @NonNull @Override public Observable<ItemId> getMaxItem()
    {
        return service.getMaxItemId(version.id);
    }

    @NonNull @Override public Observable<ItemDTO> getContent(@NonNull final ItemId itemId)
    {
        return service.getContent(version.id, itemId.id)
                .doOnNext(new Action1<ItemDTO>()
                {
                    @Override public void call(ItemDTO itemDTO)
                    {
                        quickCache.put(itemDTO);
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends ItemDTO>>()
                {
                    @Override public Observable<? extends ItemDTO> call(Throwable throwable)
                    {
                        ItemDTO cached = quickCache.get(itemId);
                        if (cached != null)
                        {
                            return Observable.just(cached);
                        }
                        return Observable.error(throwable);
                    }
                });
    }

    @NonNull @Override public Observable<UserDTO> getUser(@NonNull UserId userId)
    {
        return service.getUser(version.id, userId.id);
    }

    @NonNull @Override public Observable<UpdateDTO> getUpdates()
    {
        return service.getUpdates(version.id);
    }
}

