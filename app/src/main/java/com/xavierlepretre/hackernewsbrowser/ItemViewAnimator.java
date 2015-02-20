package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ViewAnimator;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ItemViewAnimator extends ViewAnimator
{
    @InjectView(android.R.id.progress) LoadingItemView loadingView;
    @InjectView(R.id.story) StoryView storyView;
    @InjectView(R.id.job) JobView jobView;

    private ItemViewDTO viewDTO;

    public ItemViewAnimator(Context context)
    {
        super(context);
    }

    public ItemViewAnimator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void displayItem(@NonNull ItemViewDTO item)
    {
        if (item instanceof StoryViewDTO)
        {
            setDisplayedChild(1);
            storyView.displayStory((StoryViewDTO) item);
        }
        else if (item instanceof JobViewDTO)
        {
            setDisplayedChild(2);
            jobView.displayJob((JobViewDTO) item);
        }
        else
        {
            setDisplayedChild(0);
            if (item instanceof LoadingItemViewDTO)
            {
                loadingView.displayItem((LoadingItemViewDTO) item);
            }
            else
            {
                Log.e("ItemViewAnimator", "Unhandled ItemViewDTO :" + item);
            }
        }
        this.viewDTO = item;
    }

    @Nullable public ItemViewDTO getViewDTO()
    {
        return viewDTO;
    }
}
