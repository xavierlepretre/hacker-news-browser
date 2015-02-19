package com.xavierlepretre.hackernewsbrowser;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemId;
import java.text.NumberFormat;

class LoadingItemViewDTO implements ItemViewDTO
{
    @NonNull final ItemId itemId;
    @NonNull final String title;

    LoadingItemViewDTO(@NonNull Resources resources, @NonNull ItemId itemId)
    {
        this.itemId = itemId;
        this.title = resources.getString(R.string.loading_item_id, NumberFormat.getIntegerInstance().format(itemId.id));
    }

    @NonNull @Override public ItemId getItemId()
    {
        return itemId;
    }
}
