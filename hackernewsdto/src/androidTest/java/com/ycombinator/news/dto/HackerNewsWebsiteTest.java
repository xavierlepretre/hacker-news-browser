package com.ycombinator.news.dto;

import android.os.AsyncTask;
import android.test.InstrumentationTestCase;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpStatus;

import static org.fest.assertions.api.Assertions.assertThat;

public class HackerNewsWebsiteTest extends InstrumentationTestCase
{
    private CountDownLatch signal;
    private Integer result;

    @Override protected void setUp() throws Exception
    {
        super.setUp();
        signal = new CountDownLatch(1);
        result = null;
    }

    private class TestUrl extends AsyncTask<URL, Void, Integer>
    {
        @Override protected Integer doInBackground(URL... params)
        {
            URL url = params[0];
            try
            {
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setRequestMethod("GET");
                huc.connect();
                return huc.getResponseCode();
            }
            catch (IOException e)
            {
                return -1;
            }
        }

        @Override protected void onPostExecute(Integer integer)
        {
            super.onPostExecute(integer);
            result = integer;
            signal.countDown();
        }
    }

    public void testUrlNotPresent() throws Throwable
    {
        runTestOnUiThread(new Runnable()
        {
            @Override public void run()
            {
                try
                {
                    new TestUrl().execute(new URL("http://not_a_url.com"));
                }
                catch (MalformedURLException e)
                {
                    assertTrue(false);
                }
            }
        });
        signal.await(30, TimeUnit.SECONDS);
        assertThat(result).isEqualTo(-1);
    }

    public void testUrlPresent() throws Throwable
    {
        runTestOnUiThread(new Runnable()
        {
            @Override public void run()
            {
                try
                {
                    new TestUrl().execute(new URL(ItemDTO.getOwnUrl(new ItemId(8422599))));
                }
                catch (MalformedURLException e)
                {
                    assertTrue(false);
                }
            }
        });
        signal.await(30, TimeUnit.SECONDS);
        assertThat(result).isEqualTo(HttpStatus.SC_OK);
    }
}
