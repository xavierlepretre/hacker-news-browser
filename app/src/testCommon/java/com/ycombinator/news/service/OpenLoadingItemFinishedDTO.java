package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemDTO;

public class OpenLoadingItemFinishedDTO extends LoadingItemFinishedDTO
{
    public OpenLoadingItemFinishedDTO(@NonNull ItemDTO itemDTO)
    {
        super(itemDTO);
    }
}
