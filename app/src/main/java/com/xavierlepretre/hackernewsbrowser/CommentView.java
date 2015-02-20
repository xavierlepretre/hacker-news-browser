package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.InjectView;
import com.ycombinator.news.dto.CommentDTO;

public class CommentView extends ItemView
{
    @InjectView(android.R.id.content) TextView text;

    public CommentView(Context context)
    {
        super(context);
    }

    public CommentView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CommentView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void displayComment(@NonNull CommentViewDTO commentViewDTO)
    {
        super.displayItem(commentViewDTO);
        if (text != null)
        {
            text.setText(commentViewDTO.text);
        }

        setPadding(getPaddingRight() + commentViewDTO.paddingLeft, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            setAlpha(((CommentDTO) commentViewDTO.itemDTO).isDeleted() ? 0.5f : 1);
        }
    }
}
