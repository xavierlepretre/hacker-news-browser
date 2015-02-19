package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemId;

public class LoadingItemStartedDTO implements LoadingItemDTO
{
    @NonNull public final ItemId itemId;

    LoadingItemStartedDTO(@NonNull ItemId itemId)
    {
        this.itemId = itemId;
    }
}
