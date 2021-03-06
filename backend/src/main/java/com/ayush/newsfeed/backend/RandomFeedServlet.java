package com.ayush.newsfeed.backend;

import com.ayush.newsfeed.common.internals.FlatBufferBuilder;
import com.ayush.newsfeed.common.models.Category;
import com.ayush.newsfeed.common.models.Feed;
import com.ayush.newsfeed.common.models.FeedItem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.ayush.newsfeed.backend.loremipsum.RandomLoremGenerator.writeNextDocument;
import static com.ayush.newsfeed.backend.loremipsum.RandomLoremGenerator.writeNextSentence;

/**
 * Created by dexter on 26/05/2016.
 */
public class RandomFeedServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RandomFeedServlet.class.getName());

    private static final ByteBuffer IMAGE_URL = ByteBuffer.wrap(new byte[]{
            'h', 't', 't', 'p', ':', '/', '/',
            'l', 'o', 'r', 'e', 'm', 'f', 'l',
            'i', 'c', 'k', 'r', '.', 'c', 'o',
            'm', '/', '3', '2', '0', '/', '2',
            '4', '0', '/', 'g', 'i', 'r', 'l'
    });

    private static final int DESCRIPTION_PARA_COUNT = 3; //around 3 paragraphs
    private static final int DESCRIPTION_SENTENCE_COUNT = 4; //around 4 sentences
    private static final int DESCRIPTION_WORDS_COUNT = 8; //around 10 words

    private static final int HEADING_WORDS_COUNT = 6; //around 6 words

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final FlatBufferBuilder bufferBuilder = new FlatBufferBuilder();
        //reusable buffer
        final ByteBufferOutputStream byteArrayBuffer = new ByteBufferOutputStream();

        final int feedSize = random.nextInt(10, 15); //10-15 feed items per feed
        final int headingSize = toIntSafe(req.getParameter("h"), HEADING_WORDS_COUNT);
        final int [] descriptionSize = new int[] {
                toIntSafe(req.getParameter("p"), DESCRIPTION_PARA_COUNT),
                toIntSafe(req.getParameter("s"), DESCRIPTION_SENTENCE_COUNT),
                toIntSafe(req.getParameter("w"), DESCRIPTION_WORDS_COUNT)
        };

        LOGGER.info(feedSize + " feed size");

        final int[] feedItems = new int[feedSize];

        for (int index = 0; index < feedSize; index++) {

            //create heading
            byteArrayBuffer.reset();
            writeNextSentence(headingSize, byteArrayBuffer);
            final int headingOffset = bufferBuilder.createString(byteArrayBuffer.toByteBuffer());

            //create description
            byteArrayBuffer.reset();
            writeNextDocument(descriptionSize[0], descriptionSize[1], descriptionSize[2], byteArrayBuffer);
            final int descriptionOffset = bufferBuilder.createString(byteArrayBuffer.toByteBuffer());

            //reset position for static image string
            IMAGE_URL.position(0);

            feedItems[index] = FeedItem.createFeedItem(
                    bufferBuilder,
                    random.nextLong(0, Long.MAX_VALUE),
                    headingOffset,
                    descriptionOffset,
                    System.currentTimeMillis(),
                    (byte) random.nextInt(Category.MAX),
                    bufferBuilder.createString(IMAGE_URL));
        }

        final int feedItemsOffset = Feed.createFeedItemsVector(bufferBuilder, feedItems);
        final int feed = Feed.createFeed(bufferBuilder, feedItemsOffset);
        bufferBuilder.finish(feed);

        final ByteBuffer toReturn = bufferBuilder.dataBuffer();
        final Feed feedLog = Feed.getRootAsFeed(toReturn);
        for (int index = 0; index < feedSize; index++) {

            final FeedItem item = feedLog.feedItems(index);
            LOGGER.info(item.heading());
            LOGGER.info(item.description());
            LOGGER.info(item.category() + "");
            LOGGER.info(item.curatedOn() + "");
            LOGGER.info(item.imageUrl() + "");
            LOGGER.info(item.id() + "\n");
        }
        LOGGER.info(toReturn.toString());

        resp.addHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0, proxy-revalidate, no-transform, private"); //do not cache
        resp.addHeader("Feed-Count", feedSize + "");
        resp.setContentLength(bufferBuilder.offset());
        resp.setContentType("application/octet-stream");

        //write out
        resp.getOutputStream().write(bufferBuilder.sizedByteArray());
//        bufferBuilder.writeTo(resp.getOutputStream());
    }

    private static int toIntSafe(String gg, int defaultValue) {

        if (isEmpty(gg) || !isDigitsOnly(gg))
            return defaultValue;

        try {
            return Integer.parseInt(gg);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    private static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Returns whether the given CharSequence contains only digits.
     */
    private static boolean isDigitsOnly(CharSequence str) {

        final int len = str.length();
        for (int i = 0; i < len; i++)
            if (!Character.isDigit(str.charAt(i)))
                return false;

        return true;
    }
}