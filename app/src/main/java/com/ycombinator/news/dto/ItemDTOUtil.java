package com.ycombinator.news.dto;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

public class ItemDTOUtil
{
    @NonNull public static Intent createItemBrowserIntent(@NonNull ItemDTO item)
    {
        Intent created;
        if (item instanceof StoryDTO)
        {
            created = new Intent(Intent.ACTION_VIEW, Uri.parse(((StoryDTO) item).getUrl()));
        }
        else if (item instanceof JobDTO)
        {
            created = new Intent(Intent.ACTION_VIEW, Uri.parse(((JobDTO) item).getUrl()));
        }
        else
        {
            created = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getOwnUrl()));
        }
        return created;
    }
}
