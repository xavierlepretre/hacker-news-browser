package com.xavierlepretre.util;

import android.annotation.TargetApi;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.test.AndroidTestCase;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClipboardUtilTest extends AndroidTestCase
{
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void testCopied()
    {
        ClipboardUtil.copyToClipboard(getContext(), "The quick brown fox jumps over the lazy dog");

        try
        {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(
                    Context.CLIPBOARD_SERVICE);
            assertThat(clipboard.getPrimaryClip().getItemAt(0).getText().toString()).isEqualTo("The quick brown fox jumps over the lazy dog");
        }
        catch (NoClassDefFoundError e)
        {

            //noinspection deprecation
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            assertThat(clipboard.getText().toString()).isEqualTo("The quick brown fox jumps over the lazy dog");
        }
    }
}
