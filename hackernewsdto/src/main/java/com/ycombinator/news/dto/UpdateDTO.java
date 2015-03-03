package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpdateDTO
{
    @NonNull private final List<ItemId> items;
    @NonNull private final List<UserId> profiles;

    UpdateDTO(
            @JsonProperty(value = "items", required = true) @Nullable List<ItemId> items,
            @JsonProperty(value = "profiles", required = true) @Nullable List<UserId> profiles)
    {
        this.items = Collections.unmodifiableList(items != null ? items : new ArrayList<ItemId>());
        this.profiles = Collections.unmodifiableList(profiles != null ? profiles : new ArrayList<UserId>());
    }

    @NonNull public List<ItemId> getItems()
    {
        return items;
    }

    @NonNull public List<UserId> getProfiles()
    {
        return profiles;
    }
}
