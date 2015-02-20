package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.ycombinator.news.dto.ItemDTOUtil;

public class ItemView extends RelativeLayout
{
    @InjectView(android.R.id.text1) TextView author;
    @InjectView(R.id.age) TextView age;
    @InjectView(android.R.id.button1) @Optional View openInBrowser;

    @Nullable private BaseItemViewDTO item;

    public ItemView(Context context)
    {
        super(context);
    }

    public ItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ItemView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void displayItem(@NonNull BaseItemViewDTO item)
    {
        this.item = item;
        author.setText(item.author);
        age.setText(item.age);
        if (openInBrowser != null)
        {
            openInBrowser.setVisibility(item.canOpenInBrowser ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(android.R.id.button1) @Optional
    void launchBrowser()
    {
        getContext().startActivity(getBrowserIntent());
    }

    @Nullable protected Intent getBrowserIntent()
    {
        if (item == null)
        {
            return null;
        }
        return ItemDTOUtil.createItemBrowserIntent(item.itemDTO);
    }
}
