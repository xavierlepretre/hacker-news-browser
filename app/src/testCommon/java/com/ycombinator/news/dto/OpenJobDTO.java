package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = ItemDTO.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OpenJobDTO.class, name = "job"),
})
public class OpenJobDTO extends JobDTO
{
    public OpenJobDTO(
            @NonNull ItemId id,
            @NonNull UserId by,
            @NonNull Long time,
            boolean deleted,
            @NonNull String title,
            @NonNull String url,
            int score,
            @NonNull String text)
    {
        super(id, by, time, deleted, title, url, score, text);
    }
}
