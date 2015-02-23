package com.xavierlepretre.hackernewsbrowser;

import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemId;
import rx.Observable;

public interface KidItemView
{
    @NonNull Observable<ItemId> getBackToParentObservable();
}
