package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
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
        if (item instanceof StoryView.DTO)
        {
            setDisplayedChild(1);
            storyView.displayStory((StoryView.DTO) item);
        }
        else if (item instanceof JobView.DTO)
        {
            setDisplayedChild(2);
            jobView.displayJob((JobView.DTO) item);
        }
        else
        {
            setDisplayedChild(0);
            if (item instanceof LoadingItemView.DTO)
            {
                loadingView.displayItem((LoadingItemView.DTO) item);
            }
            else
            {
                Log.e("ItemViewAnimator", "Unhandled ItemViewDTO :" + item);
            }
        }
    }
}
