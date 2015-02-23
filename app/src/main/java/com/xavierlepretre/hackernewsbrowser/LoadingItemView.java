package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewAnimator;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.ycombinator.news.dto.ItemId;
import java.text.NumberFormat;

public class LoadingItemView extends RelativeLayout
{
    @InjectView(android.R.id.title) TextView title;
    @InjectView(android.R.id.progress) ViewAnimator progressSwitcher;

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
        progressSwitcher.setDisplayedChild(item.state.childId);
        setBackgroundColor(item.bgColor);
    }

    static class DTO implements ItemViewDTO
    {
        @NonNull final ItemId itemId;
        @NonNull final String title;
        final int bgColor;
        final State state;

        DTO(@NonNull Resources resources, @NonNull ItemId itemId, State state)
        {
            this.itemId = itemId;
            this.title = resources.getString(
                    state.titleRes,
                    NumberFormat.getIntegerInstance().format(itemId.id));
            this.bgColor = resources.getColor(state.bgColorRes);
            this.state = state;
        }

        @NonNull @Override public ItemId getItemId()
        {
            return itemId;
        }
    }

    public static enum State
    {
        SCHEDULED(0, android.R.color.transparent, R.string.scheduled_item_id),
        LOADING(1, R.color.loading_item_bg, R.string.loading_item_id),
        FAILED(2, R.color.loading_failed_item_bg, R.string.loading_failed_item_id);

        private final int childId;
        @ColorRes private final int bgColorRes;
        @StringRes private final int titleRes;

        private State(int childId, @ColorRes int bgColorRes, @StringRes int titleRes)
        {
            this.childId = childId;
            this.bgColorRes = bgColorRes;
            this.titleRes = titleRes;
        }
    }
}
