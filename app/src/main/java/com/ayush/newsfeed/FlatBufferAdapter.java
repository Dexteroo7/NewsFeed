package com.ayush.newsfeed;

import android.util.Log;

import com.ayush.newsfeed.common.models.FeedItem;
import com.google.common.base.Optional;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.google.common.io.ByteStreams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

/**
 * Created by dexter on 27/05/2016.
 */
public class FlatBufferAdapter {

    private final RangeMap<Integer, CacheFile> feedRangeMap = TreeRangeMap.create();

    public void initialize(File cacheDirectory) {

        final File[] files = cacheDirectory.listFiles();
        if (files != null && files.length > 0) {

            final Queue<CacheFile> cacheFiles = new PriorityQueue<>(files.length);

            for (File file : files) {

                final Optional<CacheFile> cacheFileOptional = CacheFile.generateFrom(file);
                if (cacheFileOptional.isPresent()) {
                    cacheFiles.add(cacheFileOptional.get());
                }
            }

            int lowerEndpoint = 0, upperEndpoint = 0;
            for (CacheFile cacheFile : cacheFiles) {

                upperEndpoint += cacheFile.feedLength;
                final Range<Integer> newRange = Range.range(
                        lowerEndpoint, BoundType.CLOSED,
                        upperEndpoint, BoundType.OPEN);
                feedRangeMap.put(newRange, cacheFile);
                Log.i("Ayush", "Inserting range " + newRange.toString());

                lowerEndpoint = upperEndpoint;
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

    public FeedItem getItem(int position) {

        Log.i("Ayush", "Getting item for position: " + position);

        final Map.Entry<Range<Integer>, CacheFile> feedEntry = feedRangeMap.getEntry(position);
        assert feedEntry != null; //entry would not be null
        final int lowerEndPoint = feedEntry.getKey().lowerEndpoint();
        position -= lowerEndPoint; //offset the position for current feed

        try {
            return feedEntry.getValue().getFeed().feedItems(position);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public synchronized Range<Integer> addFeed(InputStream from,
                                               File cacheDirectory,
                                               short feedLength) {

        final long time = System.currentTimeMillis();
        final String cacheFileName = CacheFile.generateCacheFileName(feedLength, time);
        final File cacheFile = new File(cacheDirectory, cacheFileName);

        try {

            final OutputStream outputStream = new FileOutputStream(cacheFile);
            ByteStreams.copy(from, outputStream);
            outputStream.close();
        } catch (IOException e) {

            Log.i("Ayush", "Download of newsfeed failed");
            cacheFile.delete();
            cacheFile.deleteOnExit();
            throw new RuntimeException(e);
        }

        final int upperEndpoint = feedSize();
        final Range<Integer> newRange = Range.range(
                upperEndpoint, BoundType.CLOSED,
                upperEndpoint + feedLength, BoundType.OPEN);

        feedRangeMap.put(newRange, new CacheFile(cacheFile.getAbsolutePath(), time, feedLength));
        return newRange;
    }
}