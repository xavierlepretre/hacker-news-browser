package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoadingItemView extends RelativeLayout
{
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

    public void displayItem(@Nullable LoadingItemViewDTO item)
    {
        if (title != null)
        {
            if (item == null)
            {
                title.setText(R.string.loading_item);
            }
            else
            {
                title.setText(item.title);
            }
        }
    }
}
