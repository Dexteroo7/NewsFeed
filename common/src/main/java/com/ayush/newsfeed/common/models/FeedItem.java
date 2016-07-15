// automatically generated, do not modify

package com.ayush.newsfeed.common.models;

import com.ayush.newsfeed.common.internals.FlatBufferBuilder;
import com.ayush.newsfeed.common.internals.Table;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("unused")
public final class FeedItem extends Table {

    public static FeedItem getRootAsFeedItem(ByteBuffer _bb) {
        return getRootAsFeedItem(_bb, new FeedItem());
    }

    public static FeedItem getRootAsFeedItem(ByteBuffer _bb, FeedItem obj) {
        _bb.order(ByteOrder.LITTLE_ENDIAN);
        return (obj.__init(_bb.getInt(_bb.position()) + _bb.position(), _bb));
    }

    public FeedItem __init(int _i, ByteBuffer _bb) {
        bb_pos = _i;
        bb = _bb;
        return this;
    }

    public long id() {
        int o = __offset(4);
        return o != 0 ? bb.getLong(o + bb_pos) : 0;
    }

    public String heading() {
        int o = __offset(6);
        return o != 0 ? __string(o + bb_pos) : null;
    }

    public ByteBuffer headingAsByteBuffer() {
        return __vector_as_bytebuffer(6, 1);
    }

    public String description() {
        int o = __offset(8);
        return o != 0 ? __string(o + bb_pos) : null;
    }

    public ByteBuffer descriptionAsByteBuffer() {
        return __vector_as_bytebuffer(8, 1);
    }

    public long curatedOn() {
        int o = __offset(10);
        return o != 0 ? bb.getLong(o + bb_pos) : 0;
    }

    public byte category() {
        int o = __offset(12);
        return o != 0 ? bb.get(o + bb_pos) : 0;
    }

    public String imageUrl() {
        int o = __offset(14);
        return o != 0 ? __string(o + bb_pos) : null;
    }

    public ByteBuffer imageUrlAsByteBuffer() {
        return __vector_as_bytebuffer(14, 1);
    }

    public static int createFeedItem(FlatBufferBuilder builder,
                                     long id,
                                     int headingOffset,
                                     int descriptionOffset,
                                     long curatedOn,
                                     byte category,
                                     int imageUrlOffset) {

        builder.startObject(6);
        FeedItem.addCuratedOn(builder, curatedOn);
        FeedItem.addId(builder, id);
        FeedItem.addImageUrl(builder, imageUrlOffset);
        FeedItem.addDescription(builder, descriptionOffset);
        FeedItem.addHeading(builder, headingOffset);
        FeedItem.addCategory(builder, category);
        return builder.endObject();
    }

    public static void startFeedItem(FlatBufferBuilder builder) {
        builder.startObject(6);
    }

    public static void addId(FlatBufferBuilder builder, long id) {
        builder.addLong(0, id, 0);
    }

    public static void addHeading(FlatBufferBuilder builder, int headingOffset) {
        builder.addOffset(1, headingOffset, 0);
    }

    public static void addDescription(FlatBufferBuilder builder, int descriptionOffset) {
        builder.addOffset(2, descriptionOffset, 0);
    }

    public static void addCuratedOn(FlatBufferBuilder builder, long curatedOn) {
        builder.addLong(3, curatedOn, 0);
    }

    public static void addCategory(FlatBufferBuilder builder, byte category) {
        builder.addByte(4, category, 0);
    }

    public static void addImageUrl(FlatBufferBuilder builder, int imageUrlOffset) {
        builder.addOffset(5, imageUrlOffset, 0);
    }

    public static int endFeedItem(FlatBufferBuilder builder) {
        return builder.endObject();
    }
}

