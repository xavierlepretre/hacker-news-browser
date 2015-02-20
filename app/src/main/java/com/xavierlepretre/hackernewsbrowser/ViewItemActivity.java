package com.xavierlepretre.hackernewsbrowser;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.service.HackerNewsRestAdapter;
import com.ycombinator.news.service.HackerNewsService;
import java.io.IOException;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Func1;

public class ViewItemActivity extends ActionBarActivity
{
    public static final String TRANSITION_KEY_HEADER = ViewItemActivity.class.getName() + ".transitionHeader";
    private static final String KEY_ITEM_ID = ViewItemActivity.class.getName() + ".itemId";
    private static final String KEY_ITEM_DTO = ViewItemActivity.class.getName() + ".itemDto";

    public static void launch(@NonNull Activity activity,
            @NonNull View sharedElement,
            @NonNull ObjectMapper objectMapper,
            @NonNull ItemViewDTO item)
    {
        Intent viewItemIntent = new Intent(activity, ViewItemActivity.class);
        viewItemIntent.putExtra(KEY_ITEM_ID, item.getItemId().id);
        if (item instanceof BaseItemViewDTO)
        {
            try
            {
                viewItemIntent.putExtra(KEY_ITEM_DTO, objectMapper.writeValueAsString(((BaseItemViewDTO) item).itemDTO));
            }
            catch (JsonProcessingException e)
            {
                Log.e("ViewItemActivity", "Failed to serialise " + ((BaseItemViewDTO) item).itemDTO);
            }
        }
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, sharedElement, ViewItemActivity.TRANSITION_KEY_HEADER);
        ActivityCompat.startActivity(activity, viewItemIntent, options.toBundle());
    }

    ItemId showingItem;
    HackerNewsService hackerNewsService;
    ObjectMapper objectMapper;

    @InjectView(R.id.refresh_list) SwipeRefreshLayout pullToRefresh;
    @InjectView(android.R.id.list) ListView listView;
    ItemViewAnimator itemViewAnimator;

    Subscription fetchItemSubscription;

    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.hackerNewsService = HackerNewsRestAdapter.createHackerNewsService();
        this.objectMapper = HackerNewsRestAdapter.createHackerNewsMapper();

        setContentView(R.layout.activity_view_item);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ButterKnife.inject(this);

        itemViewAnimator = (ItemViewAnimator) LayoutInflater.from(this).inflate(R.layout.item_animator, null);
        this.listView.addHeaderView(itemViewAnimator, null, false);

        this.showingItem = new ItemId(getIntent().getIntExtra(KEY_ITEM_ID, 0));
        ItemViewDTO viewDTO = new LoadingItemViewDTO(
                getResources(),
                this.showingItem,
                true);
        try
        {
            String serialised = getIntent().getStringExtra(KEY_ITEM_DTO);
            if (serialised != null)
            {
                viewDTO = ItemViewDTOFactory.create(this, objectMapper.readValue(serialised, ItemDTO.class));
            }
        }
        catch (IOException e)
        {
            Log.e("ViewItemActivity", "Failed to deserialise: " + getIntent().getStringExtra(KEY_ITEM_DTO));
        }

        this.itemViewAnimator.displayItem(viewDTO);
        getSupportActionBar().setTitle(ItemViewDTOUtil.getActionBarTitle(getResources(), viewDTO));

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
        fetchItem();
    }

    @Override protected void onStop()
    {
        unsubscribe(fetchItemSubscription);
        super.onStop();
    }

    private void fetchItem()
    {
        unsubscribe(fetchItemSubscription);
        fetchItemSubscription = AppObservable.bindActivity(
                this,
                hackerNewsService.getContent(showingItem)
                        .map(new Func1<ItemDTO, ItemViewDTO>()
                        {
                            @Override public ItemViewDTO call(ItemDTO itemDTO)
                            {
                                return ItemViewDTOFactory.create(ViewItemActivity.this, itemDTO);
                            }
                        }))
                .subscribe(new Observer<ItemViewDTO>()
                {
                    @Override public void onNext(ItemViewDTO itemDTO)
                    {
                        itemViewAnimator.displayItem(itemDTO);
                        getSupportActionBar().setTitle(ItemViewDTOUtil.getActionBarTitle(getResources(), itemDTO));
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

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }
}
