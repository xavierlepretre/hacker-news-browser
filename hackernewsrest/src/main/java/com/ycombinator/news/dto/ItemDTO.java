package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private static final String BASE_URL = "https://news.ycombinator.com/item?id=%d";

    @NonNull private final ItemId id;
    @NonNull private final UserId by;
    @NonNull private final Date time;

    ItemDTO(
            @JsonProperty(value = "id", required = true) @NonNull ItemId id,
            @JsonProperty(value = "by", required = true) @NonNull UserId by,
            @JsonProperty(value = "time", required = true) @NonNull Date time)
    {
        this.id = id;
        this.by = by;
        this.time = time;
        validate();
    }

    @SuppressWarnings("ConstantConditions")
    private void validate()
    {
        if (id == null || by == null || time == null)
        {
            throw new IllegalArgumentException("Missing id=" + id + ", or by=" + by + ", or time=" + time);
        }
    }

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

    @NonNull public String getOwnUrl()
    {
        return getOwnUrl(id);
    }

    @NonNull public static String getOwnUrl(@NonNull ItemId itemId)
    {
        return String.format(BASE_URL, itemId.id);
    }
}
