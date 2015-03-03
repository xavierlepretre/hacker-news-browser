package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.firebase.client.Firebase;

public class HackerNewsServiceFirebase
{
    @NonNull final private Firebase ref;

    public HackerNewsServiceFirebase()
    {
        ref = new Firebase(HackerNewsConstants.PATH_PREFIX + "/");
    }

    @NonNull public Firebase getTopStories(@NonNull String version)
    {
        return ref.child(version + "/topstories/");
    }

    @NonNull public Firebase getMaxItemId(@NonNull String version)
    {
        return ref.child(version + "/maxitem/");
    }

    @NonNull public Firebase getContent(@NonNull String version, int itemId)
    {
        return ref.child(version  + "/item/" + itemId);
    }

    @NonNull public Firebase getUser(@NonNull String version, @NonNull String userId)
    {
        return ref.child(version + "/user/" + userId);
    }

    @NonNull public Firebase getUpdates(@NonNull String version)
    {
        return ref.child(version + "/updates");
    }
}
