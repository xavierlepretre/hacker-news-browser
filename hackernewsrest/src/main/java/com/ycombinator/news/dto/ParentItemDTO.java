package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import java.util.List;

public interface ParentItemDTO
{
    @NonNull List<ItemId> getKids();

}
