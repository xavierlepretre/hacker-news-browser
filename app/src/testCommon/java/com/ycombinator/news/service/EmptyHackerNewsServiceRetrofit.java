package com.ycombinator.news.service;

import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.UpdateDTO;
import com.ycombinator.news.dto.UserDTO;
import java.util.List;
import rx.Observable;

public class EmptyHackerNewsServiceRetrofit implements HackerNewsServiceRetrofit
{
    @Override public Observable<List<ItemId>> getTopStories(String version)
    {
        return null;
    }

    @Override public Observable<ItemId> getMaxItemId(String version)
    {
        return null;
    }

    @Override public Observable<ItemDTO> getContent(String version, int id)
    {
        return null;
    }

    @Override public Observable<UserDTO> getUser(String version, String id)
    {
        return null;
    }

    @Override public Observable<UpdateDTO> getUpdates(String version)
    {
        return null;
    }
}
