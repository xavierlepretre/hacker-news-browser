package com.ycombinator.news.service;

import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.UpdateDTO;
import com.ycombinator.news.dto.UserDTO;
import java.util.List;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface HackerNewsServiceRetrofit
{
    @GET("/{version}/topstories.json")
    Observable<List<ItemId>> getTopStories(
            @Path("version") String version);

    @GET("/{version}/maxitem.json")
    Observable<ItemId> getMaxItemId(
            @Path("version") String version);

    @GET("/{version}/item/{id}.json")
    Observable<ItemDTO> getContent(
            @Path("version") String version,
            @Path("id") long id);

    @GET("/{version}/user/{id}.json")
    Observable<UserDTO> getUser(
            @Path("version") String version,
            @Path("id") String id);

    @GET("/{version}/updates.json")
    Observable<UpdateDTO> getUpdates(
            @Path("version") String version);
}
