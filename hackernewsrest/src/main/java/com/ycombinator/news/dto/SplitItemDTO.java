package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import java.util.List;

public interface SplitItemDTO
{
    @NonNull List<ItemId> getParts();
}
