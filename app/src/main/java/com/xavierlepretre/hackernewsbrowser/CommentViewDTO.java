package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import com.ycombinator.news.dto.CommentDTO;

public class CommentViewDTO extends BaseItemViewDTO
{
    private final static int MAX_DEPTH = 2;
    @NonNull final Spanned text;
    final int paddingLeft;

    public CommentViewDTO(@NonNull Context context, @NonNull CommentDTO commentDTO, int zeroBasedDepth)
    {
        super(context, commentDTO);
        this.text = Html.fromHtml(trim(commentDTO.getText()));
        this.paddingLeft = context.getResources().getDimensionPixelSize(R.dimen.comment_indentation)
                * Math.min(MAX_DEPTH, zeroBasedDepth);
    }

    @NonNull static String trim(@NonNull String padded)
    {
        String trimmed = padded;
        int length = padded.length();
        if (length > 2)
        {
            do
            {
                trimmed = trimmed.trim();
                length = trimmed.length();

                if (trimmed.charAt(length - 1) == '\n'
                        || trimmed.charAt(length - 1) == '\t'
                        || trimmed.charAt(length - 1) == '\r')
                {
                    trimmed = trimmed.substring(0, trimmed.length() - 2);
                }

                if (trimmed.charAt(0) == '\n'
                        || trimmed.charAt(0) == '\t'
                        || trimmed.charAt(0) == '\r')
                {
                    trimmed = trimmed.substring(1, trimmed.length() - 1);
                }

                trimmed = trimmed.trim();
            }
            while (trimmed.length() < length && trimmed.length() > 2);
        }
        return trimmed;
    }
}
