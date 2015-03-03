package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.UpdateDTO;
import com.ycombinator.news.dto.UserDTO;
import com.ycombinator.news.dto.UserId;
import java.util.List;
import rx.Observable;

public interface HackerNewsService
{
    @NonNull Observable<List<ItemId>> getTopStories();

    @NonNull Observable<ItemId> getMaxItem();

    @NonNull Observable<ItemDTO> getContent(@NonNull final ItemId itemId);

    @NonNull Observable<LoadingItemDTO> getContentFromIds(@NonNull Observable<ItemId> requestedIds);

    @NonNull Observable<UserDTO> getUser(@NonNull UserId userId);

    @NonNull Observable<UpdateDTO> getUpdates();
}
