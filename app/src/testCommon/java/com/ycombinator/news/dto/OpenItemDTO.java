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
        @JsonSubTypes.Type(value = OpenItemDTO.class, name = "item"),
})
public class OpenItemDTO extends ItemDTO
{
    public OpenItemDTO(
            @NonNull ItemId id,
            @NonNull UserId by,
            @NonNull Date time,
            boolean deleted)
    {
        super(id, by, time, deleted);
    }
}
