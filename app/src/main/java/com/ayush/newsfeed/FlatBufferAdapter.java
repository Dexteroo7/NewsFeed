package com.ayush.newsfeed;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.ayush.newsfeed.common.models.Feed;
import com.ayush.newsfeed.common.models.FeedItem;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.common.io.Files;
import com.google.common.primitives.Longs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

/**
 * {0,1,2,3} , {4,5,6,7,8} , {9,10,11}
 * <p/>
 * <p/>
 * Created by dexter on 27/05/2016.
 */
public class FlatBufferAdapter {

    private final RangeMap<Integer, CacheFile> feedRangeMap = TreeRangeMap.create();
    private final File cacheDirectory;

    public FlatBufferAdapter(File directory) throws IOException {

        this.cacheDirectory = directory;

        final File[] files = this.cacheDirectory.listFiles();
        if (files != null && files.length > 0) {

            int lowerEndpoint = 0, upperEndpoint = 0;

            for (File file : files) {

                if (CacheFile.isValidFile(file)) {

                    final CacheFile cacheFile = new CacheFile(file);
                    upperEndpoint += cacheFile.feedLength;
                    final Range<Integer> newRange = Range.range(
                            lowerEndpoint, BoundType.CLOSED,
                            upperEndpoint, BoundType.OPEN);
                    feedRangeMap.put(newRange, new CacheFile(file));
                    Log.i("Ayush", "Inserting range " + newRange.toString());

                    lowerEndpoint = upperEndpoint;
                }
            }
        }
    }

    public int feedSize() {

        final Range<Integer> span;
        try {
            span = feedRangeMap.span();
        } catch (NoSuchElementException ignored) {
            return 0;
        }

        final int size = span == null || span.isEmpty() ? 0 : span.upperEndpoint();
        Log.i("Ayush", "Current size " + size);
        return size;
    }

    public FeedItem getItem(int position) throws ExecutionException {

        Log.i("Ayush", "Getting item for position: " + position);

        final Map.Entry<Range<Integer>, CacheFile> feedEntry = feedRangeMap.getEntry(position);
        assert feedEntry != null; //entry would not be null
        final int lowerEndPoint = feedEntry.getKey().lowerEndpoint();
        position -= lowerEndPoint; //offset the position for current feed

        return feedEntry.getValue().getFeed().feedItems(position);
    }

    public synchronized Range<Integer> addFeed(InputStream from,
                                               int sizeInBytes,
                                               int feedLength) throws IOException {

        final String cacheFileName = CacheFile.generateCacheFileName(feedLength);

        final File file = new File(cacheDirectory, cacheFileName);
        final MappedByteBuffer byteBuffer = Files.map(file, FileChannel.MapMode.READ_WRITE, sizeInBytes);
        for (int read = 0; read < sizeInBytes; read++) {

            final int readByte = from.read();
            if (readByte == -1) {

                Log.i("Ayush", "Read so far " + read);
                throw new IOException("Unexpected end of stream");
            }
            byteBuffer.put((byte) readByte);
        }

        //add new feed to view
        byteBuffer.flip();
        final Feed newFeed = Feed.getRootAsFeed(byteBuffer);

        int upperEndpoint;
        try {
            upperEndpoint = feedRangeMap.span().upperEndpoint();
        } catch (NoSuchElementException ignored) {
            upperEndpoint = 0;
            feedRangeMap.clear();
        }

        final Range<Integer> newRange = Range.range(
                upperEndpoint, BoundType.CLOSED,
                upperEndpoint + newFeed.feedItemsLength(), BoundType.OPEN);

        feedRangeMap.put(newRange, new CacheFile(file, newFeed));
        return newRange;
    }

    private static final class CacheFile implements Comparable<CacheFile> {

        private static String generateCacheFileName(int feedLength) {
            return "com.ayush.newsfeed_" + System.currentTimeMillis() + "_" + feedLength;
        }

        private static ByteBuffer getBuffer(String absolutePath) throws IOException {

            final RandomAccessFile accessFile = new RandomAccessFile(absolutePath, "r");
            return accessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, accessFile.length());
        }

        private static boolean isValidFile(File file) {

            if (file.exists() && file.isFile()) {

                final String[] splitter = file.getName().split("_");
                return splitter.length == 3 &&
                        splitter[0].equals("com.ayush.newsfeed") &&
                        TextUtils.isDigitsOnly(splitter[1]) &&
                        TextUtils.isDigitsOnly(splitter[2]);
            }

            return false;
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
        private final int feedLength;

//        private final Callable<Feed> LOADER = new Callable<Feed>() {
//            @Override
//            public Feed call() throws Exception {
//
//                Log.i("Ayush", "Requesting new load " + absolutePath);
//                return FEED_CACHE_LOADER.load(absolutePath);
//            }
//        };

        private CacheFile(File file) {

            this.absolutePath = file.getAbsolutePath();

            final String[] splitter = file.getName().split("_");
            this.time = Long.parseLong(splitter[1]);
            this.feedLength = Integer.parseInt(splitter[2]);
        }

        private CacheFile(File file, Feed feed) {

            this.absolutePath = file.getAbsolutePath();

            final String[] splitter = file.getName().split("_");
            this.time = Long.parseLong(splitter[1]);
            this.feedLength = Integer.parseInt(splitter[2]);

            //add as it is to cache
            FEED_LOADING_CACHE.put(file.getAbsolutePath(), feed);
        }

        @Override
        public int compareTo(@NonNull CacheFile another) {
            return Longs.compare(another.time, this.time);
        }

        private Feed getFeed() throws ExecutionException {

            return FEED_LOADING_CACHE.get(absolutePath);
        }
    }
}