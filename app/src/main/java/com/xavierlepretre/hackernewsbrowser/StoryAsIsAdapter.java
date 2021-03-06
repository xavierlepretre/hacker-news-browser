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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public class StoryAsIsAdapter extends BaseAdapter
{
    private static final int VIEW_TYPE_ITEM_LOADING = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_STORY = 2;
    private static final int VIEW_TYPE_JOB = 3;
    private static final int VIEW_TYPE_COMMENT = 4;

    @LayoutRes private static final int ITEM_LOADING_VIEW_RES = R.layout.loading_item;
    @LayoutRes private static final int ITEM_VIEW_RES = R.layout.item;
    @LayoutRes private static final int STORY_VIEW_RES = R.layout.story;
    @LayoutRes private static final int JOB_VIEW_RES = R.layout.job;
    @LayoutRes private static final int COMMENT_VIEW_RES = R.layout.comment;

    @NonNull protected final Context context;
    @NonNull private final LayoutInflater layoutInflater;
    @NonNull private List<ItemId> receivedIds;
    @NonNull private final Map<ItemId, ItemViewDTO> receivedDtos;
    @NonNull private final BehaviorSubject<ItemId> requestedIdsSubject;
    @NonNull private final BehaviorSubject<PositionedItemId> backToParentSubject;

    public StoryAsIsAdapter(@NonNull Context context)
    {
        super();
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.receivedIds = new ArrayList<>();
        this.receivedDtos = new HashMap<>();
        this.requestedIdsSubject = BehaviorSubject.create();
        this.backToParentSubject = BehaviorSubject.create();
    }

    @NonNull public Observable<ItemId> getRequestedIdsObservable()
    {
        return requestedIdsSubject.distinct();
    }

    @NonNull public Observable<PositionedItemId> getBackToParentObservable()
    {
        return backToParentSubject.asObservable();
    }

    public void setIds(@NonNull Collection<? extends ItemId> itemIds)
    {
        this.receivedIds = new ArrayList<>(itemIds);
        Resources resources = context.getResources();
        for (ItemId itemId : itemIds)
        {
            if (receivedDtos.get(itemId) == null)
            {
                receivedDtos.put(itemId, new LoadingItemView.DTO(resources, itemId, LoadingItemView.State.SCHEDULED));
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
            if (viewDTO instanceof ItemView.DTO)
            {
                collected.add(((ItemView.DTO) viewDTO).itemDTO);
            }
        }
        return collected;
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

    public void addAllViewDtos(@NonNull List<? extends ItemViewDTO> objects)
    {
        for (ItemViewDTO object : objects)
        {
            add(object);
        }
    }

    public void add(@NonNull ItemViewDTO object)
    {
        if (object instanceof ItemView.DTO
            || !(this.receivedDtos.get(object.getItemId()) instanceof ItemView.DTO))
        {
            this.receivedDtos.put(object.getItemId(), object);
        }
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
        return 5;
    }

    @NonNull public ItemViewDTO getItem(int position)
    {
        ItemId id = receivedIds.get(position);
        ItemViewDTO viewDTO = this.receivedDtos.get(id);
        if (viewDTO instanceof LoadingItemView.DTO)
        {
            requestedIdsSubject.onNext(id);
        }
        return viewDTO;
    }

    @Override public long getItemId(int position)
    {
        return receivedIds.get(position).id;
    }

    @Override public int getItemViewType(int position)
    {
        int type;
        ItemViewDTO itemViewDTO = getItem(position);
        if (itemViewDTO instanceof StoryView.DTO)
        {
            type = VIEW_TYPE_STORY;
        }
        else if (itemViewDTO instanceof JobView.DTO)
        {
            type = VIEW_TYPE_JOB;
        }
        else if (itemViewDTO instanceof CommentView.DTO)
        {
            type = VIEW_TYPE_COMMENT;
        }
        else if (itemViewDTO instanceof LoadingItemView.DTO)
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

            case VIEW_TYPE_COMMENT:
                res = COMMENT_VIEW_RES;
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
            if (convertView instanceof KidItemView)
            {
                ((KidItemView) convertView).getBackToParentObservable()
                        .map(new Func1<ItemId, PositionedItemId>()
                        {
                            @Override public PositionedItemId call(ItemId itemId)
                            {
                                return new PositionedItemId(itemId, receivedIds.indexOf(itemId));
                            }
                        })
                        .subscribe(backToParentSubject);
            }
        }
        switch (getItemViewType(position))
        {
            case VIEW_TYPE_ITEM_LOADING:
                ((LoadingItemView) convertView).displayItem((LoadingItemView.DTO) getItem(position));
                break;

            case VIEW_TYPE_STORY:
                ((StoryView) convertView).displayStory((StoryView.DTO) getItem(position));
                break;

            case VIEW_TYPE_JOB:
                ((JobView) convertView).displayJob((JobView.DTO) getItem(position));
                break;

            case VIEW_TYPE_COMMENT:
                ((CommentView) convertView).displayComment((CommentView.DTO) getItem(position));
                break;

            default:
                ((ItemView) convertView).displayItem((ItemView.DTO) getItem(position));
                break;
        }
        return convertView;
    }

    public void toggleCollapsible(@NonNull ItemId itemId)
    {
        ItemViewDTO viewDTO = receivedDtos.get(itemId);
        if (viewDTO instanceof Collapsible)
        {
            ((Collapsible) viewDTO).toggleCollapsed();
        }
    }

    public static class PositionedItemId
    {
        @NonNull public final ItemId itemId;
        public final int position;

        public PositionedItemId(@NonNull ItemId itemId, int position)
        {
            this.itemId = itemId;
            this.position = position;
        }
    }
}
