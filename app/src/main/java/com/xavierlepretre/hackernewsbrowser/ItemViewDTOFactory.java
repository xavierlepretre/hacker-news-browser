package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.JobDTO;
import com.ycombinator.news.dto.StoryDTO;

public class ItemViewDTOFactory
{
    public static BaseItemViewDTO create(@NonNull Context context, @NonNull ItemDTO itemDTO)
    {
        BaseItemViewDTO created;
        if (itemDTO instanceof StoryDTO)
        {
            created = new StoryViewDTO(context, (StoryDTO) itemDTO);
        }
        else if (itemDTO instanceof JobDTO)
        {
            created = new JobViewDTO(context, (JobDTO) itemDTO);
        }
        else
        {
            created = new BaseItemViewDTO(context, itemDTO);
        }
        return created;
    }
}
