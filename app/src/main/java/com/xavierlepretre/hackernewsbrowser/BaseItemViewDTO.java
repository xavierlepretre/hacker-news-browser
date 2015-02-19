package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import com.ocpsoft.pretty.time.PrettyTime;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemDTOUtil;
import com.ycombinator.news.dto.ItemId;
import java.util.Locale;

class BaseItemViewDTO implements ItemViewDTO
{
    @NonNull final ItemDTO itemDTO;
    @NonNull final String author;
    @NonNull final String age;
    final boolean canOpenInBrowser;

    BaseItemViewDTO(@NonNull Context context, @NonNull ItemDTO itemDTO)
    {
        this(itemDTO,
                context.getString(R.string.by_author, itemDTO.getBy().id),
                new PrettyTime(Locale.getDefault()).format(itemDTO.getTime()),
                ItemDTOUtil.createItemBrowserIntent(itemDTO).resolveActivity(context.getPackageManager()) != null);
    }

    BaseItemViewDTO(@NonNull ItemDTO itemDTO, @NonNull String author, @NonNull String age, boolean canOpenInBrowser)
    {
        this.itemDTO = itemDTO;
        this.author = author;
        this.age = age;
        this.canOpenInBrowser = canOpenInBrowser;
    }

    @NonNull @Override public ItemId getItemId()
    {
        return itemDTO.getId();
    }
}
