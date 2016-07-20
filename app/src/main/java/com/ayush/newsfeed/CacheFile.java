package com.ayush.newsfeed;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.ayush.newsfeed.common.models.Feed;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutionException;

/**
 * Created by dexter on 20/07/2016.
 */
final class CacheFile implements Comparable<CacheFile> {

    static String generateCacheFileName(int feedLength, long time) {
        return "com.ayush.newsfeed_" + time + "_" + feedLength;
    }

    private static ByteBuffer getBuffer(String absolutePath) throws IOException {

        final File file = new File(absolutePath);
        return new FileInputStream(file)
                .getChannel()
                .map(FileChannel.MapMode.READ_ONLY, 0, file.length());
    }

    static Optional<CacheFile> generateFrom(File file) {

        if (file.exists() && file.isFile() && file.length() > 0) {

            final String[] splitter = file.getName().split("_");
            if (splitter.length == 3 &&
                    splitter[0].equals("com.ayush.newsfeed") &&
                    TextUtils.isDigitsOnly(splitter[1]) &&
                    TextUtils.isDigitsOnly(splitter[2])) {

                final String absolutePath = file.getAbsolutePath();
                final long time = Long.parseLong(splitter[1]);
                final short feedLength = Short.parseShort(splitter[2]);

                return Optional.of(new CacheFile(absolutePath, time, feedLength));
            }
        }

        return Optional.absent();
    }

    ///////////////////////////////////
    ///////////////////////////////////

    private static final CacheLoader<String, Feed> FEED_CACHE_LOADER = new CacheLoader<String, Feed>() {
        @Override
        public Feed load(String pathToFile) throws IOException {

            Log.i("Ayush", "Mapping new cacheFile " + pathToFile);
            return Feed.getRootAsFeed(getBuffer(pathToFile));
        }
    };

    private static final LoadingCache<String, Feed> FEED_LOADING_CACHE = CacheBuilder.newBuilder()
            .initialCapacity(3)
            .maximumSize(3)
            .softValues()
            .build(FEED_CACHE_LOADER);

    private final String absolutePath;
    private final long time;
    final short feedLength;

    CacheFile(String absolutePath, long time, short feedLength) {

        this.absolutePath = absolutePath;
        this.time = time;
        this.feedLength = feedLength;
        //FEED_LOADING_CACHE.put(absolutePath, feed);
    }

    @Override
    public int compareTo(@NonNull CacheFile another) {

        return Long.compare(another.time, this.time);
    }

    Feed getFeed() throws ExecutionException {

        return FEED_LOADING_CACHE.get(absolutePath);
    }
}