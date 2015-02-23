package com.xavierlepretre.hackernewsbrowser;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.WindowManager;
import com.ycombinator.news.dto.ItemDTOUtil;

public class ItemViewDTOUtil
{
    @NonNull public static String getActionBarTitle(@NonNull Resources resources, @NonNull ItemViewDTO item)
    {
        String title;
        if (item instanceof ItemView.DTO)
        {
            title = ItemDTOUtil.getActionBarTitle(((ItemView.DTO) item).itemDTO);
        }
        else if (item instanceof LoadingItemView.DTO)
        {
            title = ((LoadingItemView.DTO) item).title;
        }
        else
        {
            title = resources.getString(android.R.string.unknownName);
        }
        return title;
    }

    public static int getDisplayWidthPixel(@NonNull Context context)
    {
        return getDisplayWidthPixel(((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay());
    }

    public static int getDisplayWidthPixel(@NonNull Display display)
    {
        int width;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            width = getDisplayWidthPixel13AndLater(display);
        }
        else
        {
            width = getDisplayWidthPixel12AndBefore(display);
        }
        return width;
    }

    public static int getDisplayWidthPixel12AndBefore(@NonNull Display display)
    {
        //noinspection deprecation
        return display.getWidth();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static int getDisplayWidthPixel13AndLater(@NonNull Display display)
    {
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @NonNull public static String getCopyableText(@NonNull ItemViewDTO item)
    {
        String copied;
        if (item instanceof ItemView.DTO)
        {
            copied = ItemDTOUtil.getCopyableText(((ItemView.DTO) item).itemDTO);
        }
        else
        {
            copied = item.getItemId().id.toString();
        }
        return copied;
    }
}
