package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemId;

public class LoadingItemFailedDTO implements LoadingItemDTO
{
    @NonNull public final ItemId itemId;

    LoadingItemFailedDTO(@NonNull ItemId itemId)
    {
        this.itemId = itemId;
    }

    @NonNull @Override public ItemId getItemId()
    {
        return itemId;
    }
}
