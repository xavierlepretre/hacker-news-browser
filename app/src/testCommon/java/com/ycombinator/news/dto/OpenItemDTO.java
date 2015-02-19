package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import java.util.Date;

public class OpenItemDTO extends ItemDTO
{
    public OpenItemDTO(
            @NonNull ItemId id,
            @NonNull UserId by,
            @NonNull Date time)
    {
        super(id, by, time);
    }
}
