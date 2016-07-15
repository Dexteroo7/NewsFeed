package com.ayush.newsfeed;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ayush.newsfeed.common.models.FeedItem;
import com.google.common.collect.Range;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    private static final Request REQUEST = new Request.Builder()
            .url("https://reach-1200.appspot.com/randomFeedServlet")
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build();

    @Nullable
    private static FlatBufferAdapter flatBufferAdapter;

    private static File cacheDirectory;

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    private final Adapter adapter = new Adapter() {

        @Override
        public FeedItem getItem(int position) {

            assert flatBufferAdapter != null;
            assert cacheDirectory != null;

            if (position > flatBufferAdapter.feedSize() - 3)
                new FetchFeed(adapter).executeOnExecutor(EXECUTOR_SERVICE, cacheDirectory);

            try {
                return flatBufferAdapter.getItem(position);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int getItemCount() {
            return flatBufferAdapter != null ? flatBufferAdapter.feedSize() : 0;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setSmoothScrollbarEnabled(true);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        assert recyclerView != null;
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        cacheDirectory = getCacheDir();

        new FetchFeed(adapter).executeOnExecutor(EXECUTOR_SERVICE, cacheDirectory);
    }

    private static final class FetchFeed extends AsyncTask<File, Void, Range<Integer>> {

        private final Adapter adapter;

        private FetchFeed(Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        protected Range<Integer> doInBackground(File... params) {

            try {

                final Response response = OK_HTTP_CLIENT.newCall(REQUEST).execute();
                final int feedCount = Integer.parseInt(response.header("Feed-Count", "0"));
                final ResponseBody body = response.body();

                Log.i("Ayush", "Feed count header " + feedCount);
                Log.i("Ayush", "Feed length header " + body.contentLength());

                if (feedCount == 0)
                    return Range.singleton(0);

                if (flatBufferAdapter == null)
                    flatBufferAdapter = new FlatBufferAdapter(params[0]);

                return flatBufferAdapter.addFeed(
                        body.byteStream(),
                        (int) body.contentLength(),
                        feedCount);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Ayush", e.getLocalizedMessage());
            }

            return Range.singleton(0);
        }

        @Override
        protected void onPostExecute(Range<Integer> feed) {

            super.onPostExecute(feed);

            if (!feed.isEmpty())
                adapter.notifyItemRangeInserted(feed.lowerEndpoint(), feed.upperEndpoint());
        }
    }
}