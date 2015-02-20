package com.xavierlepretre.hackernewsbrowser;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemDTOUtil;

public class ItemViewDTOUtil
{
    @NonNull public static String getActionBarTitle(@NonNull Resources resources, @NonNull ItemViewDTO item)
    {
        String title;
        if (item instanceof BaseItemViewDTO)
        {
            title = ItemDTOUtil.getActionBarTitle(((BaseItemViewDTO) item).itemDTO);
        }
        else if (item instanceof LoadingItemViewDTO)
        {
            title = ((LoadingItemViewDTO) item).title;
        }
        else
        {
            title = resources.getString(android.R.string.unknownName);
        }
        return title;
    }
}
