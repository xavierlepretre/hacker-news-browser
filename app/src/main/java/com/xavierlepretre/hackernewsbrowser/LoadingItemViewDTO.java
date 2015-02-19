package com.xavierlepretre.hackernewsbrowser;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemId;
import java.text.NumberFormat;

class LoadingItemViewDTO implements ItemViewDTO
{
    @NonNull final ItemId itemId;
    @NonNull final String title;
    final boolean loading;

    LoadingItemViewDTO(@NonNull Resources resources, @NonNull ItemId itemId, boolean loading)
    {
        this.itemId = itemId;
        this.title = resources.getString(
                loading ? R.string.loading_item_id : R.string.scheduled_item_id,
                NumberFormat.getIntegerInstance().format(itemId.id));
        this.loading = loading;
    }

    @NonNull @Override public ItemId getItemId()
    {
        return itemId;
    }
}
