package com.xavierlepretre.hackernewsbrowser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xavierlepretre.util.ClipboardUtil;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.dto.ParentItemDTO;
import com.ycombinator.news.dto.ParentKidMap;
import com.ycombinator.news.service.HackerNewsRestAdapter;
import com.ycombinator.news.service.HackerNewsService;
import com.ycombinator.news.service.LoadingItemDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class ViewItemActivity extends ActionBarActivity
{
    public static final String TRANSITION_KEY_HEADER = ViewItemActivity.class.getName() + ".transitionHeader";
    static final String KEY_ITEM_ID = ViewItemActivity.class.getName() + ".itemId";
    static final String KEY_ITEM_DTO = ViewItemActivity.class.getName() + ".itemDto";

    public static void launch(@NonNull Activity activity,
            @NonNull View sharedElement,
            @NonNull ObjectMapper objectMapper,
            @NonNull ItemViewDTO item)
    {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, sharedElement, ViewItemActivity.TRANSITION_KEY_HEADER);
        ActivityCompat.startActivity(activity, createLaunchIntent(activity, objectMapper, item), options.toBundle());
    }

    static Intent createLaunchIntent(@NonNull Context context,
            @NonNull ObjectMapper objectMapper,
            @NonNull ItemViewDTO item)
    {
        Intent viewItemIntent = new Intent(context, ViewItemActivity.class);
        viewItemIntent.putExtra(KEY_ITEM_ID, item.getItemId().id);
        if (item instanceof ItemView.DTO)
        {
            try
            {
                viewItemIntent.putExtra(KEY_ITEM_DTO, objectMapper.writeValueAsString(((ItemView.DTO) item).itemDTO));
            }
            catch (JsonProcessingException e)
            {
                Log.e("ViewItemActivity", "Failed to serialise " + ((ItemView.DTO) item).itemDTO);
            }
        }
        return viewItemIntent;
    }

    ItemId showingItem;
    @NonNull ParentKidMap parentKidMap;
    @NonNull HackerNewsService hackerNewsService;
    @NonNull ObjectMapper objectMapper;
    @NonNull BehaviorSubject<ItemId> requiredIdsSubject;

    @InjectView(R.id.refresh_list) SwipeRefreshLayout pullToRefresh;
    @InjectView(android.R.id.list) ListView listView;
    ItemViewAnimator itemViewAnimator;
    @NonNull StoryAsIsAdapter commentAdapter;

    Subscription fetchItemSubscription;
    Subscription fetchKidsSubscription;
    Subscription listenRequiredSubscription;
    Subscription listenBackParentSubscription;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.parentKidMap = new ParentKidMap();
        this.hackerNewsService = HackerNewsRestAdapter.createHackerNewsService();
        this.objectMapper = HackerNewsRestAdapter.createHackerNewsMapper();
        this.requiredIdsSubject = BehaviorSubject.create();

        setContentView(R.layout.activity_view_item);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ButterKnife.inject(this);

        this.showingItem = new ItemId(getIntent().getIntExtra(KEY_ITEM_ID, 0));
        ItemViewDTO viewDTO = new LoadingItemView.DTO(
                getResources(),
                this.showingItem,
                LoadingItemView.State.LOADING);
        String serialised = getIntent().getStringExtra(KEY_ITEM_DTO);
        if (serialised != null)
        {
            try
            {
                viewDTO = ItemViewDTOFactory.create(this, objectMapper.readValue(serialised, ItemDTO.class));
                parentKidMap.put(((ItemView.DTO) viewDTO).itemDTO);
            }
            catch (IOException e)
            {
                Log.e("ViewItemActivity", "Failed to deserialise: " + getIntent().getStringExtra(KEY_ITEM_DTO));
            }
        }

        View itemViewAnimatorContainer = LayoutInflater.from(this).inflate(R.layout.item_animator, null);
        itemViewAnimator = (ItemViewAnimator) (itemViewAnimatorContainer.findViewById(android.R.id.extractArea));
        this.listView.addHeaderView(
                itemViewAnimatorContainer,
                viewDTO,
                true);

        this.itemViewAnimator.displayItem(viewDTO);
        getSupportActionBar().setTitle(ItemViewDTOUtil.getActionBarTitle(getResources(), viewDTO));

        this.commentAdapter = new StoryAsIsAdapter(this);
        this.commentAdapter.setIds(parentKidMap.flattenPrimoGeniture(showingItem));
        this.listView.setAdapter(commentAdapter);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                fetchItem();
            }
        });
        ViewCompat.setTransitionName(itemViewAnimator, TRANSITION_KEY_HEADER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_stories, menu);
        return true;
    }

    @Override protected void onStart()
    {
        super.onStart();
        pullToRefresh.setRefreshing(true);
        fetchItem();
        fetchKids();
        listenToRequiredIds();
        listenToBackToParent();
    }

    @Override protected void onStop()
    {
        unsubscribe(listenBackParentSubscription);
        unsubscribe(listenRequiredSubscription);
        unsubscribe(fetchKidsSubscription);
        unsubscribe(fetchItemSubscription);
        super.onStop();
    }

    private void fetchItem()
    {
        unsubscribe(fetchItemSubscription);
        fetchItemSubscription = AppObservable.bindActivity(
                this,
                hackerNewsService.getContent(showingItem)
                        .observeOn(Schedulers.computation())
                        .map(new Func1<ItemDTO, Pair<List<ItemId>, ItemViewDTO>>()
                        {
                            @Override public Pair<List<ItemId>, ItemViewDTO> call(ItemDTO itemDTO)
                            {
                                parentKidMap.put(itemDTO);
                                ItemViewDTO viewDTO =
                                        ItemViewDTOFactory.create(ViewItemActivity.this, itemDTO, parentKidMap.getCollapsibleState(itemDTO.getId()));
                                return Pair.create(parentKidMap.flattenPrimoGeniture(showingItem), viewDTO);
                            }
                        })
                        .onErrorResumeNext(new Func1<Throwable, Observable<? extends Pair<List<ItemId>, ItemViewDTO>>>()
                        {
                            @Override public Observable<? extends Pair<List<ItemId>, ItemViewDTO>> call(Throwable throwable)
                            {
                                return Observable.just(Pair.create((List<ItemId>) new ArrayList<ItemId>(),
                                        (ItemViewDTO) new LoadingItemView.DTO(getResources(), showingItem, LoadingItemView.State.FAILED)));
                            }
                        }))
                .subscribe(new Observer<Pair<List<ItemId>, ItemViewDTO>>()
                {
                    @Override public void onNext(Pair<List<ItemId>, ItemViewDTO> viewPair)
                    {
                        pullToRefresh.setRefreshing(false);
                        itemViewAnimator.displayItem(viewPair.second);
                        getSupportActionBar().setTitle(ItemViewDTOUtil.getActionBarTitle(getResources(), viewPair.second));
                        commentAdapter.setIds(viewPair.first);
                    }

                    @Override public void onCompleted()
                    {
                    }

                    @Override public void onError(Throwable e)
                    {
                        Log.e("ViewItemActivity", "Failed to load main item", e);
                    }
                });
    }

    private void fetchKids()
    {
        unsubscribe(fetchKidsSubscription);
        fetchKidsSubscription = AppObservable.bindActivity(
                this,
                hackerNewsService.getContentFromIds(requiredIdsSubject)
                        .observeOn(Schedulers.computation())
                        .map(new Func1<LoadingItemDTO, Pair<List<ItemId>, ItemViewDTO>>()
                        {
                            @Override public Pair<List<ItemId>, ItemViewDTO> call(LoadingItemDTO loadingItemDTO)
                            {
                                ItemViewDTO viewDTO = ItemViewDTOFactory.create(ViewItemActivity.this, loadingItemDTO,
                                        parentKidMap.getCollapsibleState(loadingItemDTO.getItemId()));
                                if (viewDTO instanceof ItemView.DTO)
                                {
                                    parentKidMap.put(((ItemView.DTO) viewDTO).itemDTO);
                                }
                                return Pair.create(
                                        parentKidMap.flattenPrimoGeniture(showingItem),
                                        viewDTO);
                            }
                        }))
                .subscribe(new Observer<Pair<List<ItemId>, ItemViewDTO>>()
                {
                    @Override public void onNext(Pair<List<ItemId>, ItemViewDTO> viewPair)
                    {
                        commentAdapter.add(viewPair.second);
                        commentAdapter.setIds(viewPair.first);
                    }

                    @Override public void onCompleted()
                    {
                    }

                    @Override public void onError(Throwable e)
                    {
                        Log.e("ViewItemActivity", "Failed to load kid item", e);
                    }
                });
    }

    private void listenToRequiredIds()
    {
        unsubscribe(listenRequiredSubscription);
        listenRequiredSubscription = AppObservable.bindActivity(
                this,
                commentAdapter.getRequestedIdsObservable())
                .subscribe(requiredIdsSubject);
    }

    private void listenToBackToParent()
    {
        unsubscribe(listenBackParentSubscription);
        listenBackParentSubscription = AppObservable.bindActivity(
                this,
                commentAdapter.getBackToParentObservable())
                .subscribe(new Observer<StoryAsIsAdapter.PositionedItemId>()
                {
                    @Override public void onNext(StoryAsIsAdapter.PositionedItemId o)
                    {
                        listView.smoothScrollToPosition(o.position + listView.getHeaderViewsCount());
                    }

                    @Override public void onCompleted()
                    {
                    }

                    @Override public void onError(Throwable e)
                    {
                        Log.e("ViewItemActivity", "Failed to scroll back to parent", e);
                    }
                });
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(android.R.id.list) void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        ItemViewDTO clicked = (ItemViewDTO) parent.getItemAtPosition(position);
        if (clicked instanceof ItemView.DTO
                && ((ItemView.DTO) clicked).itemDTO instanceof ParentItemDTO
                && ((ParentItemDTO) ((ItemView.DTO) clicked).itemDTO).getKids().isEmpty())
        {
            Toast.makeText(this, R.string.no_comment_collapse_description, Toast.LENGTH_SHORT).show();
        }
        else if (clicked instanceof LoadingItemView.DTO)
        {
            Toast.makeText(this, R.string.retry_download, Toast.LENGTH_SHORT).show();
            requiredIdsSubject.onNext(clicked.getItemId());
        }

        if (clicked.getItemId().equals(showingItem) && !(clicked instanceof LoadingItemView.DTO))
        {
            Toast.makeText(this, R.string.cannot_collapse_main_comments, Toast.LENGTH_LONG).show();
        }
        else
        {
            toggleCollapsedState(clicked.getItemId());
        }
    }

    public void toggleCollapsedState(@NonNull final ItemId itemId)
    {
        AppObservable.bindActivity(this, Observable.just(itemId)
                .subscribeOn(Schedulers.computation())
                .map(new Func1<ItemId, List<ItemId>>()
                {
                    @Override public List<ItemId> call(ItemId itemId)
                    {
                        parentKidMap.toggleCollapsedState(itemId);
                        return parentKidMap.flattenPrimoGeniture(showingItem);
                    }
                }))
                .subscribe(new Observer<List<ItemId>>()
                {
                    @Override public void onNext(List<ItemId> newIds)
                    {
                        commentAdapter.toggleCollapsible(itemId);
                        commentAdapter.setIds(newIds);
                    }

                    @Override public void onCompleted()
                    {
                    }

                    @Override public void onError(Throwable e)
                    {
                    }
                });
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemLongClick(android.R.id.list) boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        ItemViewDTO viewDTO = (ItemViewDTO) parent.getItemAtPosition(position);
        String copyable = ItemViewDTOUtil.getCopyableText(viewDTO);
        ClipboardUtil.copyToClipboard(this, copyable);
        Toast.makeText(this, getString(R.string.copied_to_clipboard, copyable), Toast.LENGTH_LONG).show();
        return true;
    }

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }
}
