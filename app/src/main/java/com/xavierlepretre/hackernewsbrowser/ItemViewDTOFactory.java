package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import com.ycombinator.news.dto.CommentDTO;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.JobDTO;
import com.ycombinator.news.dto.StoryDTO;
import com.ycombinator.news.service.LoadingItemDTO;
import com.ycombinator.news.service.LoadingItemFinishedDTO;
import com.ycombinator.news.service.LoadingItemStartedDTO;
import java.util.ArrayList;
import java.util.List;

public class ItemViewDTOFactory
{
    @NonNull public static ItemViewDTO create(@NonNull Context context, LoadingItemDTO loadingItemDTO)
    {
        ItemViewDTO created;
        if (loadingItemDTO instanceof LoadingItemFinishedDTO)
        {
            created = create(context, ((LoadingItemFinishedDTO) loadingItemDTO).itemDTO);
        }
        else if (loadingItemDTO instanceof LoadingItemStartedDTO)
        {
            created = new LoadingItemViewDTO(context.getResources(), ((LoadingItemStartedDTO) loadingItemDTO).itemId, true);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type:" + loadingItemDTO.getClass());
        }
        return created;
    }

    @NonNull public static List<ItemViewDTO> create(@NonNull Context context, @NonNull List<? extends ItemDTO> itemDTOs)
    {
        List<ItemViewDTO> created = new ArrayList<>();
        for (ItemDTO itemDTO : itemDTOs)
        {
            created.add(create(context, itemDTO));
        }
        return created;
    }

    @NonNull public static BaseItemViewDTO create(@NonNull Context context, @NonNull ItemDTO itemDTO)
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
        else if (itemDTO instanceof CommentDTO)
        {
            created = new CommentViewDTO(context, (CommentDTO) itemDTO, 0); // TODO better?
        }
        else
        {
            created = new BaseItemViewDTO(context, itemDTO);
        }
        return created;
    }
}
