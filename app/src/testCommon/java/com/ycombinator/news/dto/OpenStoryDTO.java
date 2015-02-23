package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Date;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = ItemDTO.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OpenStoryDTO.class, name = "story"),
})
public class OpenStoryDTO extends StoryDTO
{
    public OpenStoryDTO(
            @NonNull ItemId id,
            @NonNull UserId by,
            @NonNull Date time,
            boolean deleted,
            @NonNull String title,
            @NonNull String url,
            int score,
            @Nullable List<ItemId> kids)
    {
        super(id, by, time, deleted, title, url, score, kids);
    }
}
