package com.ycombinator.news.dto;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParentKidMap extends LinkedHashMap<ItemId, List<ItemId>> // parentId -> kidIds
{
    @NonNull final Map<ItemId, CollapsedState> collapsibleStates;

    public ParentKidMap()
    {
        collapsibleStates = new HashMap<>();
    }

    public void put(@NonNull ItemDTO itemDTO)
    {
        ItemId itemId = itemDTO.getId();
        CollapsedState collapsedState = collapsibleStates.get(itemId);
        if (collapsedState == null)
        {
            CollapsedState parentCollapsedState = null;
            if (itemDTO instanceof KidItemDTO)
            {
                ItemId parentId = ((KidItemDTO) itemDTO).getParent();
                parentCollapsedState = collapsibleStates.get(parentId);
            }

            if (parentCollapsedState != null)
            {
                collapsedState = new CollapsedState(parentCollapsedState.zeroBasedDepth + 1);
            }
            else
            {
                collapsedState = new CollapsedState(false);
            }
            collapsibleStates.put(itemId, collapsedState);
        }
        if (itemDTO instanceof ParentItemDTO)
        {
            put(itemId, (ParentItemDTO) itemDTO, collapsedState);
        }
    }

    void put(@NonNull ItemId parentId, @NonNull ParentItemDTO parentItemDTO, @NonNull CollapsedState parentState)
    {
        super.put(parentId, parentItemDTO.getKids());
        for (ItemId kidId : parentItemDTO.getKids())
        {
            CollapsedState kidState = collapsibleStates.get(kidId);
            if (kidState == null)
            {
                collapsibleStates.put(kidId, new CollapsedState(parentState.zeroBasedDepth + 1));
            }
        }
    }

    @Override public List<ItemId> put(ItemId key, List<ItemId> value)
    {
        throw new IllegalArgumentException("Not allowed");
    }

    @Override public void putAll(Map<? extends ItemId, ? extends List<ItemId>> map)
    {
        throw new IllegalArgumentException("Not allowed");
    }

    @Nullable public CollapsedState getCollapsibleState(@NonNull ItemId itemId)
    {
        return collapsibleStates.get(itemId);
    }

    public boolean toggleCollapsedState(@NonNull ItemId itemId)
    {
        boolean result;
        CollapsedState state = getCollapsibleState(itemId);
        if (state != null)
        {
            state.toggleCollapsed();
            result = true;
        }
        else
        {
            result = false;
        }
        return result;
    }

    @NonNull public List<ItemId> flattenPrimoGeniture(@NonNull ItemId parentId)
    {
        List<ItemId> flattened = new ArrayList<>();
        CollapsedState parentCollapsedState = collapsibleStates.get(parentId);
        if (parentCollapsedState != null && !parentCollapsedState.isCollapsed())
        {
            List<ItemId> kids = get(parentId);
            CollapsedState collapsible;
            if (kids != null)
            {
                for (ItemId kidId : kids)
                {
                    collapsible = collapsibleStates.get(kidId);
                    if (collapsible != null)
                    {
                        flattened.add(kidId);
                        flattened.addAll(flattenPrimoGeniture(kidId));
                    }
                }
            }
        }
        return flattened;
    }
}
