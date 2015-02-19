package com.xavierlepretre.hackernewsbrowser;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemDTOList;
import com.ycombinator.news.dto.ItemId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoryAsIsAdapter extends BaseAdapter
{
    private static final int VIEW_TYPE_ITEM_LOADING = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_STORY = 2;
    private static final int VIEW_TYPE_JOB = 3;

    @LayoutRes private static final int ITEM_LOADING_VIEW_RES = R.layout.loading_item;
    @LayoutRes private static final int ITEM_VIEW_RES = R.layout.item;
    @LayoutRes private static final int STORY_VIEW_RES = R.layout.story;
    @LayoutRes private static final int JOB_VIEW_RES = R.layout.job;

    @NonNull private Context context;
    @NonNull private LayoutInflater layoutInflater;
    @NonNull private List<ItemId> receivedIds;
    @NonNull private Map<ItemId, ItemViewDTO> receivedDtos;

    public StoryAsIsAdapter(@NonNull Context context)
    {
        super();
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.receivedIds = new ArrayList<>();
        this.receivedDtos = new HashMap<>();
    }

    public void setIds(@NonNull List<ItemId> itemIds)
    {
        this.receivedIds = itemIds;
        Resources resources = context.getResources();
        for (ItemId itemId : itemIds)
        {
            if (receivedDtos.get(itemId) == null)
            {
                receivedDtos.put(itemId, new LoadingItemViewDTO(resources, itemId, false));
            }
        }
        notifyDataSetChanged();
    }

    @NonNull List<ItemId> getIds()
    {
        return new ArrayList<>(receivedIds);
    }

    @NonNull ItemDTOList getReceivedDtos()
    {
        ItemDTOList collected = new ItemDTOList();
        for (ItemViewDTO viewDTO : receivedDtos.values())
        {
            if (viewDTO instanceof BaseItemViewDTO)
            {
                collected.add(((BaseItemViewDTO) viewDTO).itemDTO);
            }
        }
        return collected;
    }

    public void setStartedLoading(@NonNull ItemId itemId)
    {
        if (!(receivedDtos.get(itemId) instanceof BaseItemViewDTO))
        {
            receivedDtos.put(itemId, new LoadingItemViewDTO(context.getResources(), itemId, true));
        }
    }

    public void addAll(@NonNull List<? extends ItemDTO> objects)
    {
        for (ItemDTO object : objects)
        {
            add(object);
        }
    }

    public void add(@NonNull ItemDTO object)
    {
        this.receivedDtos.put(object.getId(), ItemViewDTOFactory.create(context, object));
    }

    @Override public boolean hasStableIds()
    {
        return true;
    }

    @Override public int getCount()
    {
        return this.receivedIds.size();
    }

    @Override public int getViewTypeCount()
    {
        return 4;
    }

    @NonNull public ItemViewDTO getItem(int position)
    {
        return this.receivedDtos.get(receivedIds.get(position));
    }

    @Override public long getItemId(int position)
    {
        return receivedIds.get(position).id;
    }

    @Override public int getItemViewType(int position)
    {
        int type;
        ItemViewDTO itemViewDTO = getItem(position);
        if (itemViewDTO instanceof StoryViewDTO)
        {
            type = VIEW_TYPE_STORY;
        }
        else if (itemViewDTO instanceof JobViewDTO)
        {
            type = VIEW_TYPE_JOB;
        }
        else if (itemViewDTO instanceof LoadingItemViewDTO)
        {
            type = VIEW_TYPE_ITEM_LOADING;
        }
        else
        {
            type = VIEW_TYPE_ITEM;
        }
        return type;
    }

    @LayoutRes private int getItemViewRes(int position)
    {
        int res;
        switch (getItemViewType(position))
        {
            case VIEW_TYPE_ITEM_LOADING:
                res = ITEM_LOADING_VIEW_RES;
                break;

            case VIEW_TYPE_STORY:
                res = STORY_VIEW_RES;
                break;

            case VIEW_TYPE_JOB:
                res = JOB_VIEW_RES;
                break;

            default:
                res = ITEM_VIEW_RES;
                break;
        }
        return res;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = layoutInflater.inflate(getItemViewRes(position), null);
        }
        switch (getItemViewType(position))
        {
            case VIEW_TYPE_ITEM_LOADING:
                ((LoadingItemView) convertView).displayItem((LoadingItemViewDTO) getItem(position));
                break;

            case VIEW_TYPE_STORY:
                ((StoryView) convertView).displayStory((StoryViewDTO) getItem(position));
                break;

            case VIEW_TYPE_JOB:
                ((JobView) convertView).displayJob((JobViewDTO) getItem(position));
                break;

            default:
                ((ItemView) convertView).displayItem((BaseItemViewDTO) getItem(position));
                break;
        }
        return convertView;
    }

    @NonNull public List<ItemId> keepUnknown(@NonNull List<ItemId> fromList)
    {
        List<ItemId> unknown = new ArrayList<>(fromList);
        for (ItemViewDTO viewDTO : receivedDtos.values())
        {
            if (viewDTO instanceof BaseItemViewDTO)
            {
                unknown.remove(viewDTO.getItemId());
            }
        }
        return unknown;
    }
}
