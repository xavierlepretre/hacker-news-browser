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
import com.ocpsoft.pretty.time.PrettyTime;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemDTOUtil;
import com.ycombinator.news.dto.ItemId;
import java.util.Locale;

public class ItemView extends RelativeLayout
{
    @InjectView(android.R.id.text1) TextView author;
    @InjectView(R.id.age) TextView age;
    @InjectView(android.R.id.button1) @Optional View openInBrowser;

    @Nullable private DTO item;

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

    @Nullable public DTO getItem()
    {
        return item;
    }

    public void displayItem(@NonNull DTO item)
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

    static class DTO implements ItemViewDTO
    {
        @NonNull final ItemDTO itemDTO;
        @NonNull final String author;
        @NonNull final String age;
        final boolean canOpenInBrowser;

        DTO(@NonNull Context context, @NonNull ItemDTO itemDTO)
        {
            this(itemDTO,
                    itemDTO.getBy().id,
                    new PrettyTime(Locale.getDefault()).format(itemDTO.getTime()),
                    ItemDTOUtil.createItemBrowserIntent(itemDTO).resolveActivity(context.getPackageManager()) != null);
        }

        DTO(@NonNull ItemDTO itemDTO, @NonNull String author, @NonNull String age, boolean canOpenInBrowser)
        {
            this.itemDTO = itemDTO;
            this.author = author;
            this.age = age;
            this.canOpenInBrowser = canOpenInBrowser;
        }

        @NonNull @Override public ItemId getItemId()
        {
            return itemDTO.getId();
        }
    }
}
