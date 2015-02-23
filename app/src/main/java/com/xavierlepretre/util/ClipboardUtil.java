package com.xavierlepretre.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

public class ClipboardUtil
{
    public static void copyToClipboard(@NonNull Context context, @NonNull String toCopy)
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
        {
            copyToClipboard11AndLater(context, toCopy);
        }
        else
        {
            copyToClipboard10AndBefore(context, toCopy);
        }
    }

    private static void copyToClipboard10AndBefore(@NonNull Context context, @NonNull String toCopy)
    {
        @SuppressWarnings("deprecation")
        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(toCopy);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void copyToClipboard11AndLater(@NonNull Context context, @NonNull String toCopy)
    {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", toCopy);
        clipboard.setPrimaryClip(clip);
    }
}
