package com.xavierlepretre.hackernewsbrowser;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.dto.ItemDTOList;
import com.ycombinator.news.dto.ItemId;
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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TopStoriesActivity extends ActionBarActivity
{
    private final static String KEY_IDS = TopStoriesActivity.class.getName() + ".ids";
    private final static String KEY_DTOS = TopStoriesActivity.class.getName() + ".dtos";

    HackerNewsService hackerNewsService;
    StoryAsIsAdapter adapter;
    ObjectMapper objectMapper;

    Subscription deserialiseSavedInstanceSubscription;
    Subscription topStoriesSubscription;

    @InjectView(R.id.refresh_list) SwipeRefreshLayout pullToRefresh;
    @InjectView(android.R.id.list) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_stories);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.hackerNewsService = HackerNewsRestAdapter.createHackerNewsService();
        this.objectMapper = HackerNewsRestAdapter.createHackerNewsMapper();
        this.adapter = new StoryAsIsAdapter(this);
        ButterKnife.inject(this);
        listView.setAdapter(adapter);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                fetchTopStories();
            }
        });

        if (savedInstanceState != null)
        {
            try
            {
                if (savedInstanceState.containsKey(KEY_IDS))
                {
                    this.adapter.setIds(objectMapper.readValue(savedInstanceState.getString(KEY_IDS), ItemIdList.class));
                }
            }
            catch (IOException e)
            {
                Log.e("TopStoriesActivity", "Failed to read ids", e);
            }
            if (savedInstanceState.containsKey(KEY_DTOS))
            {
                deserialiseSavedInstanceSubscription = Observable.just(savedInstanceState.getString(KEY_DTOS))
                        .subscribeOn(Schedulers.computation())
                        .map(new Func1<String, List<ItemViewDTO>>()
                        {
                            @Override public List<ItemViewDTO> call(String serialised)
                            {
                                try
                                {
                                    return ItemViewDTOFactory.create(
                                            TopStoriesActivity.this,
                                            objectMapper.readValue(serialised, ItemDTOList.class));
                                }
                                catch (IOException e)
                                {
                                    throw new RuntimeException(e);
                                }
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<List<ItemViewDTO>>()
                        {
                            @Override public void onNext(List<ItemViewDTO> itemViewDTOs)
                            {
                                adapter.addAllViewDtos(itemViewDTOs);
                                adapter.notifyDataSetChanged();
                            }

                            @Override public void onCompleted()
                            {
                            }

                            @Override public void onError(Throwable e)
                            {
                                Log.e("TopStoriesActivity", "Failed to read dtos", e);
                            }
                        });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_stories, menu);
        getSupportActionBar().setTitle(getString(R.string.top_stories_label));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override protected void onStart()
    {
        super.onStart();
        fetchTopStories();
    }

    @Override protected void onStop()
    {
        unsubscribe(this.topStoriesSubscription);
        super.onStop();
    }

    @Override protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        try
        {
            outState.putString(KEY_IDS, objectMapper.writeValueAsString(adapter.getIds()));
        }
        catch (JsonProcessingException e)
        {
            Log.e("TopStoriesActivity", "Failed to serialise ids", e);
        }
        try
        {
            outState.putString(KEY_DTOS, objectMapper.writeValueAsString(adapter.getReceivedDtos()));
        }
        catch (JsonProcessingException e)
        {
            Log.e("TopStoriesActivity", "Failed to serialise dtos", e);
        }
    }

    @Override protected void onDestroy()
    {
        unsubscribe(deserialiseSavedInstanceSubscription);
        this.adapter = null;
        this.hackerNewsService = null;
        ButterKnife.reset(this);
        super.onDestroy();
    }

    private void fetchTopStories()
    {
        if (this.topStoriesSubscription != null)
        {
            this.topStoriesSubscription.unsubscribe();
        }
        pullToRefresh.setRefreshing(true);
        this.topStoriesSubscription = AppObservable.bindActivity(
                this,
                hackerNewsService.getTopStories()
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Func1<List<ItemId>, Observable<LoadingItemDTO>>()
                        {
                            @Override public Observable<LoadingItemDTO> call(List<ItemId> itemIds)
                            {
                                adapter.setIds(itemIds);
                                pullToRefresh.setRefreshing(false);
                                return hackerNewsService.getContentFromIds(adapter.getRequestedIdsObservable());
                            }
                        })
                        .observeOn(Schedulers.computation())
                        .map(new Func1<LoadingItemDTO, ItemViewDTO>()
                        {
                            @Override public ItemViewDTO call(LoadingItemDTO loadingItemDTO)
                            {
                                return ItemViewDTOFactory.create(TopStoriesActivity.this, loadingItemDTO);
                            }
                        }))
                .subscribe(
                        new Observer<ItemViewDTO>()
                        {
                            @Override public void onNext(ItemViewDTO itemDTO)
                            {
                                adapter.add(itemDTO);
                                adapter.notifyDataSetChanged();
                            }

                            @Override public void onCompleted()
                            {
                                pullToRefresh.setRefreshing(false);
                            }

                            @Override public void onError(Throwable e)
                            {
                                Log.e("TopStoriesActivity", "Failed to fetch item", e);
                                pullToRefresh.setRefreshing(false);
                            }
                        });
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(android.R.id.list)
    void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        ViewItemActivity.launch(this,
                view.findViewById(android.R.id.extractArea),
                objectMapper,
                (ItemViewDTO) parent.getItemAtPosition(position));
    }

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    private static class ItemIdList extends ArrayList<ItemId>
    {
        ItemIdList()
        {
            super();
        }
    }
}
