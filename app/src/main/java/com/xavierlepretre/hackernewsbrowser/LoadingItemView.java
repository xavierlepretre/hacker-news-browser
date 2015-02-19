package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoadingItemView extends RelativeLayout
{
    @ColorRes private static final int SCHEDULED_BG_COLOR_RES = android.R.color.transparent;
    @ColorRes private static final int LOADING_BG_COLOR_RES = R.color.loading_item_bg;

    @InjectView(android.R.id.title) TextView title;

    public LoadingItemView(Context context)
    {
        super(context);
    }

    public LoadingItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LoadingItemView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void displayItem(@NonNull LoadingItemViewDTO item)
    {
        if (title != null)
        {
            title.setText(item.title);
        }
        setBackgroundColor(getResources().getColor(item.loading ? LOADING_BG_COLOR_RES : SCHEDULED_BG_COLOR_RES));
    }
}
