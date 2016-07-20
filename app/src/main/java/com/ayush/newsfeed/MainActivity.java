package com.ayush.newsfeed;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.ayush.newsfeed.common.models.FeedItem;
import com.google.common.collect.Range;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private static final Range<Integer> EMPTY_RANGE = Range.singleton(0);
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    private static final Request REQUEST = new Request.Builder()
            .url("https://reach-1200.appspot.com/randomFeedServlet")
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build();

    private final FlatBufferAdapter flatBufferAdapter = new FlatBufferAdapter();

    private final Adapter adapter = new Adapter() {

        @Override
        public FeedItem getItem(int position) {

            if (position > flatBufferAdapter.feedSize() - 5)
                new FetchFeed().execute(getCacheDir());
            return flatBufferAdapter.getItem(position);
        }

        @Override
        public int getItemCount() {

            final int feedSize = flatBufferAdapter.feedSize();
            if (feedSize == 0)
                new FetchFeed().execute(getCacheDir());
            return feedSize;
        }
    };

    @Nullable
    private static WeakReference<Adapter> adapterWeakReference = null;
    @Nullable
    private static WeakReference<FlatBufferAdapter> flatBufferAdapterWeakReference = null;

    {
        adapterWeakReference = new WeakReference<>(adapter);
        flatBufferAdapterWeakReference = new WeakReference<>(flatBufferAdapter);
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize adapter
        flatBufferAdapter.initialize(getCacheDir());

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setSmoothScrollbarEnabled(true);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        assert recyclerView != null;
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setBackground(null);
    }

    private static final class FetchFeed extends AsyncTask<File, Void, Range<Integer>> {

        @NonNull
        @Override
        protected Range<Integer> doInBackground(File... params) {

            final Response response;
            try {
                response = OK_HTTP_CLIENT.newCall(REQUEST).execute();
            } catch (IOException e) {

                e.printStackTrace();
                Log.i("Ayush", e.getLocalizedMessage());
                return EMPTY_RANGE;
            }

            final short feedCount = Short.parseShort(response.header("Feed-Count", "0"));
            final ResponseBody body = response.body();

            Log.i("Ayush", "Feed count header " + feedCount);
            Log.i("Ayush", "Feed length header " + body.contentLength());

            if (feedCount == 0)
                return EMPTY_RANGE;

            return MiscUtils.useReference(flatBufferAdapterWeakReference, adapter -> {
                return adapter.addFeed(
                        body.byteStream(),
                        params[0],
                        feedCount);
            }).or(EMPTY_RANGE);
        }

        @Override
        protected void onPostExecute(@NonNull Range<Integer> feed) {

            super.onPostExecute(feed);

            if (feed != EMPTY_RANGE && !feed.isEmpty()) {

                MiscUtils.useReference(adapterWeakReference, adapter -> {
                    adapter.notifyItemRangeInserted(feed.lowerEndpoint(), feed.upperEndpoint());
                });
            }
        }
    }
}