package com.ycombinator.news.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;

public interface QuickCache
{
    void put(@NonNull ItemDTO itemDTO);

    @Nullable ItemDTO get(@NonNull ItemId itemId);

    void clear();

    public static class Instance
    {
        // Poorman's dependency injection
        public static QuickCache get()
        {
            return QuickCacheImpl.getInstance();
        }
    }
}
