package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Date;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = ItemDTO.class,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = JobDTO.class, name = "job"),
        @JsonSubTypes.Type(value = StoryDTO.class, name = "story"),
        @JsonSubTypes.Type(value = CommentDTO.class, name = "comment"),
        @JsonSubTypes.Type(value = PollDTO.class, name = "poll"),
        @JsonSubTypes.Type(value = PollOptDTO.class, name = "pollopt"),
})
public class ItemDTO
{
    @NonNull ItemId id;
    @NonNull UserId by;
    @NonNull Date time;

    @NonNull public ItemId getId()
    {
        return id;
    }

    @NonNull public UserId getBy()
    {
        return by;
    }

    @NonNull public Date getTime()
    {
        return time;
    }
}
