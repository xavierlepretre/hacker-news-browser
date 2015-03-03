package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.firebase.client.DataSnapshot;
import com.firebase.client.rx.FirebaseObservable;
import com.ycombinator.news.cache.QuickCache;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.UpdateDTO;
import com.ycombinator.news.dto.UserDTO;
import com.ycombinator.news.dto.UserId;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class HackerNewsServiceImpl extends HackerNewsThrottledService
{
    @NonNull private final HackerNewsServiceFirebase hackerNewsServiceFirebase;
    @NonNull private final ApiVersion version;
    @NonNull private final QuickCache quickCache;
    @NonNull private final Func1<DataSnapshot, ItemDTO> itemDTODeserialiser;
    @NonNull private final Func1<DataSnapshot, List<ItemId>> itemIdListDeserialiser;
    @NonNull private final Func1<DataSnapshot, ItemId> itemIdDeserialiser;
    @NonNull private final Func1<DataSnapshot, UserDTO> userDTODeserialiser;
    @NonNull private final Func1<DataSnapshot, UpdateDTO> updateDTODeserialiser;

    public HackerNewsServiceImpl(
            @NonNull HackerNewsServiceFirebase hackerNewsServiceFirebase,
            @NonNull ApiVersion version,
            @NonNull QuickCache quickCache) // TODO handle cache
    {
        this.hackerNewsServiceFirebase = hackerNewsServiceFirebase;
        this.version = version;
        this.quickCache = quickCache;
        this.itemDTODeserialiser = new DataSnapshotMapper<>(ItemDTO.class);
        this.itemIdListDeserialiser = new Func1<DataSnapshot, List<ItemId>>()
        {
            @Override public List<ItemId> call(DataSnapshot dataSnapshot)
            {
                return dataSnapshot.getValue(ItemIdList.class);
            }
        };
        this.itemIdDeserialiser = new DataSnapshotMapper<>(ItemId.class);
        this.userDTODeserialiser = new DataSnapshotMapper<>(UserDTO.class);
        this.updateDTODeserialiser = new DataSnapshotMapper<>(UpdateDTO.class);
    }

    @NonNull public Observable<List<ItemId>> getTopStories()
    {
        return FirebaseObservable.read(hackerNewsServiceFirebase.getTopStories(version.id))
                .map(itemIdListDeserialiser);
    }

    @NonNull @Override public Observable<ItemId> getMaxItem()
    {
        return FirebaseObservable.read(hackerNewsServiceFirebase.getMaxItemId(version.id))
                .map(itemIdDeserialiser);
    }

    @NonNull @Override public Observable<ItemDTO> getContent(@NonNull final ItemId itemId)
    {
        return FirebaseObservable.read(hackerNewsServiceFirebase.getContent(version.id, itemId.id))
                .map(itemDTODeserialiser)
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
        return FirebaseObservable.read(hackerNewsServiceFirebase.getUser(version.id, userId.id))
                .map(userDTODeserialiser);
    }

    @NonNull @Override public Observable<UpdateDTO> getUpdates()
    {
        return FirebaseObservable.read(hackerNewsServiceFirebase.getUpdates(version.id))
                .map(updateDTODeserialiser);
    }

    private static class ItemIdList extends ArrayList<ItemId>
    {
        ItemIdList()
        {
            super();
        }
    }
}
