package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.ycombinator.news.dto.ItemId;
import java.text.NumberFormat;

public class LoadingItemView extends RelativeLayout
{
    private static final int LOADING_VIEW_CHILD_SCHEDULED = 0;
    private static final int LOADING_VIEW_CHILD_LOADING = 1;

    @ColorRes private static final int SCHEDULED_BG_COLOR_RES = android.R.color.transparent;
    @ColorRes private static final int LOADING_BG_COLOR_RES = R.color.loading_item_bg;

    @InjectView(android.R.id.title) TextView title;
    @InjectView(android.R.id.progress) ViewSwitcher progressSwitcher;

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

    public void displayItem(@NonNull DTO item)
    {
        title.setText(item.title);
        progressSwitcher.setDisplayedChild(item.progressChildId);
        setBackgroundColor(item.bgColor);
    }

    static class DTO implements ItemViewDTO
    {
        @NonNull final ItemId itemId;
        @NonNull final String title;
        final int progressChildId;
        final int bgColor;
        final boolean loading;

        DTO(@NonNull Resources resources, @NonNull ItemId itemId, boolean loading)
        {
            this.itemId = itemId;
            this.title = resources.getString(
                    loading ? R.string.loading_item_id : R.string.scheduled_item_id,
                    NumberFormat.getIntegerInstance().format(itemId.id));
            this.progressChildId = loading ? LOADING_VIEW_CHILD_LOADING : LOADING_VIEW_CHILD_SCHEDULED;
            this.bgColor = resources.getColor(loading ? LOADING_BG_COLOR_RES : SCHEDULED_BG_COLOR_RES);
            this.loading = loading;
        }

        @NonNull @Override public ItemId getItemId()
        {
            return itemId;
        }
    }
}
