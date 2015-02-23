package com.ycombinator.news.dto;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import java.text.NumberFormat;

public class ItemDTOUtil
{
    @NonNull public static Intent createItemBrowserIntent(@NonNull ItemDTO item)
    {
        Intent created;
        if (item instanceof WithUrlDTO)
        {
            created = new Intent(Intent.ACTION_VIEW, Uri.parse(((WithUrlDTO) item).getUrl()));
        }
        else
        {
            created = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getOwnUrl()));
        }
        return created;
    }

    @NonNull public static String getActionBarTitle(@NonNull ItemDTO item)
    {
        String title;
        if (item instanceof TitledDTO)
        {
            title = ((TitledDTO) item).getTitle();
        }
        else if (item instanceof TextedDTO)
        {
            title = ((TextedDTO) item).getText();
        }
        else
        {
            title = NumberFormat.getInstance().format(item.getId().id);
        }
        return title;
    }

    @NonNull public static String getCopyableText(@NonNull ItemDTO item)
    {
        String copied;
        if (item instanceof TextedDTO)
        {
            copied = ((TextedDTO) item).getText();
        }
        else if (item instanceof WithUrlDTO)
        {
            copied = ((WithUrlDTO) item).getUrl();
        }
        else if (item instanceof TitledDTO)
        {
            copied = ((TitledDTO) item).getTitle();
        }
        else
        {
            copied = item.getOwnUrl();
        }
        return copied;
    }
}
