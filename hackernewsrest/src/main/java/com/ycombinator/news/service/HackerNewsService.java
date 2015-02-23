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
import rx.subjects.ReplaySubject;

public class HackerNewsService
{
    public final static int PARALLEL_COUNT_CONTENT = 3;

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

    @NonNull public Observable<LoadingItemDTO> getContent(@NonNull Iterable<? extends ItemId> itemIds)
    {
        return getContent(itemIds, PARALLEL_COUNT_CONTENT);
    }

    @NonNull public Observable<LoadingItemDTO> getContent(@NonNull Iterable<? extends ItemId> itemIds, int maxConcurrent)
    {
        return getContentFromIds(Observable.from(itemIds), maxConcurrent);
    }

    @NonNull public Observable<LoadingItemDTO> getContentFromIds(@NonNull Observable<ItemId> requestedIds)
    {
        return getContentFromIds(requestedIds, PARALLEL_COUNT_CONTENT);
    }

    @NonNull public Observable<LoadingItemDTO> getContentFromIds(@NonNull Observable<ItemId> requestedIds, int maxConcurrent)
    {
        final ReplaySubject<LoadingItemDTO> subject = ReplaySubject.create();
        final Func1<ItemId, Observable<ItemDTO>> properCall = new Func1<ItemId, Observable<ItemDTO>>()
        {
            @Override public Observable<ItemDTO> call(final ItemId itemId)
            {
                // We need to go one step deeper otherwise the subscriber is overwhelmed
                subject.onNext(new LoadingItemStartedDTO(itemId));
                return getContent(itemId)
                        .onErrorResumeNext(new Func1<Throwable, Observable<? extends ItemDTO>>()
                        {
                            @Override public Observable<? extends ItemDTO> call(Throwable throwable)
                            {
                                subject.onNext(new LoadingItemFailedDTO(itemId));
                                return Observable.empty();
                            }
                        });
            }
        };
        final Func1<ItemId, Observable<ItemDTO>> preventImmediateCall = new Func1<ItemId, Observable<ItemDTO>>()
        {
            // These methods will be called immediately, they are not part of the maxConcurrent
            @Override public Observable<ItemDTO> call(ItemId itemId)
            {
                return Observable.just(itemId).flatMap(properCall);
            }
        };
        getContent(requestedIds.map(preventImmediateCall), maxConcurrent)
                .subscribe(subject);
        return subject.asObservable();
    }

    @NonNull public Observable<LoadingItemDTO> getContent(@NonNull Observable<Observable<ItemDTO>> contentFetchers, int maxConcurrent)
    {
        return Observable.merge(
                contentFetchers,
                maxConcurrent)
                .map(new Func1<ItemDTO, LoadingItemDTO>()
                {
                    @Override public LoadingItemDTO call(ItemDTO itemDTO)
                    {
                        return new LoadingItemFinishedDTO(itemDTO);
                    }
                });
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

