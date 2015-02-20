package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.UpdateDTO;
import com.ycombinator.news.dto.UserDTO;
import com.ycombinator.news.dto.UserId;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.ReplaySubject;

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

    @NonNull public Observable<LoadingItemDTO> getContent(@NonNull Iterable<? extends ItemId> itemIds)
    {
        return getContent(itemIds, PARALLEL_COUNT_STORY);
    }

    @NonNull public Observable<LoadingItemDTO> getContent(@NonNull Iterable<? extends ItemId> itemIds, int maxConcurrent)
    {
        final ReplaySubject<LoadingItemDTO> subject = ReplaySubject.create();
        List<Observable<ItemDTO>> contentFetchers = new ArrayList<>();
        for (final ItemId itemId : itemIds)
        {
            contentFetchers.add(Observable.just(itemId)
                    .flatMap(new Func1<ItemId, Observable<ItemDTO>>()
                    {
                        @Override public Observable<ItemDTO> call(ItemId itemId)
                        {
                            subject.onNext(new LoadingItemStartedDTO(itemId));
                            return getContent(itemId);
                        }
                    }));
        }
        getContent(Observable.from(contentFetchers), maxConcurrent)
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

