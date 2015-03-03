package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
            @NonNull Long time,
            boolean deleted,
            @NonNull String title,
            @NonNull String url,
            int score,
            @Nullable String text,
            @Nullable List<ItemId> kids,
            int descendants)
    {
        super(id, by, time, deleted, title, url, score, text, kids, descendants);
    }
}
