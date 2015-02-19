package com.ycombinator.news.service;

import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemId;

public class OpenLoadingItemStartedDTO extends LoadingItemStartedDTO
{
    public OpenLoadingItemStartedDTO(@NonNull ItemId itemId)
    {
        super(itemId);
    }
}
