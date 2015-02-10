package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.UserDTO;
import com.ycombinator.news.dto.UserId;
import java.util.List;
import rx.Observable;

public class HackerNewsService
{
    @NonNull final HackerNewsServiceRetrofit service;
    @NonNull final ApiVersion version;

    public HackerNewsService(@NonNull HackerNewsServiceRetrofit service,
            @NonNull ApiVersion version)
    {
        this.service = service;
        this.version = version;
    }

    @NonNull public  Observable<List<ItemId>> getTopStories()
    {
        return service.getTopStories(version.id);
    }

    @NonNull public  Observable<ItemDTO> getContent(@NonNull ItemId itemId)
    {
        return service.getContent(version.id, itemId.id);
    }

    @NonNull public  Observable<UserDTO> getUser(@NonNull UserId userId)
    {
        return service.getUser(version.id, userId.id);
    }

}
