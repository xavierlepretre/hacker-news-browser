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
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class HackerNewsService
{
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

    @NonNull public Observable<ItemDTO> getContent(@NonNull List<ItemId> itemIds, int parallelCount)
    {
        List<List<ItemId>> split = splitList(itemIds, parallelCount);
        int index = 0;
        Observable<ItemDTO> merged = getContent(split.get(index++));
        while (index < parallelCount)
        {
            merged = merged.mergeWith(getContent(split.get(index++)));
        }
        return merged;
    }

    @NonNull static List<List<ItemId>> splitList(@NonNull List<ItemId> itemIds, int splitCount)
    {
        List<List<ItemId>> split = new ArrayList<>();
        for (int i = 0; i < splitCount; i++)
        {
            split.add(new ArrayList<ItemId>());
        }
        int index = 0;
        for (ItemId itemId : itemIds)
        {
            split.get(index++ % splitCount).add(itemId);
        }
        return split;
    }

    @NonNull public Observable<ItemDTO> getContent(@NonNull final Iterable<? extends ItemId> itemIds)
    {
        return Observable.create(
                new Observable.OnSubscribe<ItemDTO>()
                {
                    @Override public void call(Subscriber<? super ItemDTO> subscriber)
                    {
                        try
                        {
                            for (ItemId itemId : itemIds)
                            {
                                subscriber.onNext(service.getContentSync(version.id, itemId.id));
                            }
                            subscriber.onCompleted();
                        }
                        catch (Throwable e)
                        {
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.io());
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

