package com.xavierlepretre.hackernewsbrowser;

import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemId;

public interface ItemViewDTO
{
    @NonNull ItemId getItemId();
}
