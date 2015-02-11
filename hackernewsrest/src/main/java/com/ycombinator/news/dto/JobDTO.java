package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

public class JobDTO extends ItemDTO
{
    JobDTO(
            @JsonProperty(value = "id", required = true) @NonNull ItemId id,
            @JsonProperty(value = "by", required = true) @NonNull UserId by,
            @JsonProperty(value = "time", required = true) @NonNull Date time)
    {
        super(id, by, time);
    }
}
