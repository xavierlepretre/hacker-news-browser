package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemId;

public interface LoadingItemDTO
{
    @NonNull ItemId getItemId();
}
