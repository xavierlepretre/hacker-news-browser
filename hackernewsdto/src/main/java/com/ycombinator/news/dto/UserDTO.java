package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UserDTO
{
    @NonNull private final UserId id;
    private final int karma;
    private final int delay;
    @NonNull private final Date created;
    @NonNull private final String about;
    @NonNull private final List<ItemId> submitted;

    UserDTO(
            @JsonProperty(value = "id", required = true) @NonNull UserId id,
            @JsonProperty(value = "karma") int karma,
            @JsonProperty(value = "delay") int delay,
            @JsonProperty(value = "created", required = true) @NonNull Date created,
            @JsonProperty(value = "about", required = true) @NonNull String about,
            @JsonProperty(value = "submitted") @Nullable List<ItemId> submitted)
    {
        this.id = id;
        this.karma = karma;
        this.delay = delay;
        this.created = created;
        this.about = about;
        this.submitted = Collections.unmodifiableList(submitted != null ? submitted : new ArrayList<ItemId>());
        validate();
    }

    @SuppressWarnings("ConstantConditions")
    private void validate()
    {
        if (id == null || created == null || about == null)
        {
            throw new IllegalArgumentException("Missing id=" + id + ", or created=" + created + ", or about=" + about);
        }
    }

    @NonNull public UserId getId()
    {
        return id;
    }

    public int getKarma()
    {
        return karma;
    }

    public int getDelay()
    {
        return delay;
    }

    @NonNull public Date getCreated()
    {
        return created;
    }

    @NonNull public String getAbout()
    {
        return about;
    }

    @NonNull public List<ItemId> getSubmitted()
    {
        return submitted;
    }
}
