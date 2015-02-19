package com.xavierlepretre.hackernewsbrowser;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ycombinator.news.dto.ItemDTO;
import com.ycombinator.news.dto.ItemDTOList;
import com.ycombinator.news.dto.ItemId;
import com.ycombinator.news.service.HackerNewsRestAdapter;
import com.ycombinator.news.service.HackerNewsService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class TopStoriesActivity extends ActionBarActivity
{
    private final static String KEY_IDS = "TopStoriesActivity.ids";
    private final static String KEY_DTOS = "TopStoriesActivity.dtos";

    HackerNewsService hackerNewsService;
    StoryAsIsAdapter adapter;
    ObjectMapper objectMapper;

    Subscription topStoriesSubscription;

    @InjectView(R.id.refresh_list) SwipeRefreshLayout pullToRefresh;
    @InjectView(android.R.id.list) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_stories);
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
                try
                {
                    adapter.addAll(objectMapper.readValue(savedInstanceState.getString(KEY_DTOS), ItemDTOList.class));
                    adapter.notifyDataSetChanged();
                }
                catch (IOException e)
                {
                    Log.e("TopStoriesActivity", "Failed to read dtos", e);
                }
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
        this.topStoriesSubscription.unsubscribe();
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
                        .doOnNext(new Action1<List<ItemId>>()
                        {
                            @Override public void call(List<ItemId> itemIds)
                            {
                                adapter.setIds(itemIds);
                            }
                        })
                        .observeOn(Schedulers.io())
                        .flatMap(new Func1<List<ItemId>, Observable<ItemDTO>>()
                        {
                            @Override public Observable<ItemDTO> call(List<ItemId> itemIds)
                            {
                                return hackerNewsService.getContent(adapter.keepUnknown(itemIds));
                            }
                        }))
                .subscribe(
                        new Observer<ItemDTO>()
                        {
                            @Override public void onNext(ItemDTO itemDTO)
                            {
                                adapter.add(itemDTO);
                                adapter.notifyDataSetChanged();
                            }

                            @Override public void onCompleted()
                            {
                                adapter.notifyDataSetChanged();
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
    @OnItemClick(android.R.id.list) void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Toast.makeText(this, "Push " + ((ItemViewDTO) parent.getItemAtPosition(position)).getItemId().id, Toast.LENGTH_SHORT)
                .show();
    }

    private static class ItemIdList extends ArrayList<ItemId>
    {
        ItemIdList()
        {
            super();
        }
    }
}
