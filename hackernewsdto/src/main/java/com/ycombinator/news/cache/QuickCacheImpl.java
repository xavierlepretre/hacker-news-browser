package com.ycombinator.news.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;

/**
 * Pardon how rudimentary it is
 */
class QuickCacheImpl implements QuickCache
{
    private static int DEFAULT_ITEM_CACHE_SIZE = 200;
    static QuickCacheImpl instance;

    @NonNull private final LruCache<ItemId, ItemDTO> itemCache;

    QuickCacheImpl()
    {
        this(DEFAULT_ITEM_CACHE_SIZE);
    }

    QuickCacheImpl(int maxSize)
    {
        this.itemCache = new LruCache<>(maxSize);
    }

    @NonNull public static QuickCacheImpl getInstance()
    {
        if (instance == null)
        {
            instance = new QuickCacheImpl();
        }
        return instance;
    }

    @Override public void put(@NonNull ItemDTO itemDTO)
    {
        itemCache.put(itemDTO.getId(), itemDTO);
    }

    @Nullable @Override public ItemDTO get(@NonNull ItemId itemId)
    {
        return itemCache.get(itemId);
    }

    @Override public void clear()
    {
        itemCache.evictAll();
    }
}
