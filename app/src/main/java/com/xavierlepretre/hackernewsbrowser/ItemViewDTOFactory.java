package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ycombinator.news.dto.CollapsedState;
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
        return create(context, loadingItemDTO, null);
    }

    @NonNull public static ItemViewDTO create(@NonNull Context context, LoadingItemDTO loadingItemDTO, @Nullable CollapsedState collapsible)
    {
        ItemViewDTO created;
        if (loadingItemDTO instanceof LoadingItemFinishedDTO)
        {
            created = create(context, ((LoadingItemFinishedDTO) loadingItemDTO).itemDTO, collapsible);
        }
        else if (loadingItemDTO instanceof LoadingItemStartedDTO)
        {
            created = new LoadingItemView.DTO(context.getResources(), ((LoadingItemStartedDTO) loadingItemDTO).itemId, true);
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

    @NonNull public static ItemView.DTO create(@NonNull Context context, @NonNull ItemDTO itemDTO)
    {
        return create(context, itemDTO, 0, false);
    }

    @NonNull public static ItemView.DTO create(@NonNull Context context, @NonNull ItemDTO itemDTO, @Nullable CollapsedState collapsible)
    {
        return create(
                context,
                itemDTO,
                collapsible == null ? 0 : collapsible.zeroBasedDepth,
                collapsible != null && collapsible.isCollapsed());
    }

    @NonNull public static ItemView.DTO create(@NonNull Context context, @NonNull ItemDTO itemDTO, int zeroBasedDepth, boolean collapsed)
    {
        ItemView.DTO created;
        if (itemDTO instanceof StoryDTO)
        {
            created = new StoryView.DTO(context, (StoryDTO) itemDTO);
        }
        else if (itemDTO instanceof JobDTO)
        {
            created = new JobView.DTO(context, (JobDTO) itemDTO);
        }
        else if (itemDTO instanceof CommentDTO)
        {
            created = new CommentView.DTO(context, (CommentDTO) itemDTO, zeroBasedDepth, collapsed);
        }
        else
        {
            created = new ItemView.DTO(context, itemDTO);
        }
        return created;
    }
}
