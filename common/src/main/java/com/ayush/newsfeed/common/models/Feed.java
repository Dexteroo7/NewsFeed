// automatically generated, do not modify

package com.ayush.newsfeed.common.models;

import com.ayush.newsfeed.common.internals.FlatBufferBuilder;
import com.ayush.newsfeed.common.internals.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class Feed extends Table {

    public static Feed getRootAsFeed(ByteBuffer _bb) {
        return getRootAsFeed(_bb, new Feed());
    }

    public static Feed getRootAsFeed(ByteBuffer _bb, Feed obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb));
    }

    public Feed __init(int _i, ByteBuffer _bb) {
        bb_pos = _i;
        bb = _bb;
        return this;
    }

    public FeedItem feedItems(int j) {
        return feedItems(new FeedItem(), j);
    }

    public FeedItem feedItems(FeedItem obj, int j) {
        int o = __offset(4);
        return o != 0 ? obj.__init(__indirect(__vector(o) + j * 4), bb) : null;
    }

    public int feedItemsLength() {
        int o = __offset(4);
        return o != 0 ? __vector_len(o) : 0;
    }

    public static int createFeed(FlatBufferBuilder builder,
                                 int feedItemsOffset) {
        builder.startObject(1);
        Feed.addFeedItems(builder, feedItemsOffset);
        return builder.endObject();
    }

    public static void startFeed(FlatBufferBuilder builder) {
        builder.startObject(1);
    }

    public static void addFeedItems(FlatBufferBuilder builder, int feedItemsOffset) {
        builder.addOffset(0, feedItemsOffset, 0);
    }

    public static int createFeedItemsVector(FlatBufferBuilder builder, int[] data) {

        builder.startVector(4, data.length, 4);
        for (int i = data.length - 1; i >= 0; i--)
            builder.addOffset(data[i]);
        return builder.endVector();
    }

    public static void startFeedItemsVector(FlatBufferBuilder builder, int numElems) {
        builder.startVector(4, numElems, 4);
    }

    public static int endFeed(FlatBufferBuilder builder) {
        return builder.endObject();
    }

    public static void finishFeedBuffer(FlatBufferBuilder builder, int offset) {
        builder.finish(offset);
    }
}
