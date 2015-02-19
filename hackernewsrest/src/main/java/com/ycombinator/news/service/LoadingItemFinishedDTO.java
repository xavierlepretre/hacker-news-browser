package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemDTO;

public class LoadingItemFinishedDTO implements LoadingItemDTO
{
    @NonNull public final ItemDTO itemDTO;

    LoadingItemFinishedDTO(@NonNull ItemDTO itemDTO)
    {
        this.itemDTO = itemDTO;
    }
}
