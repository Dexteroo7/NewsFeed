package com.ayush.newsfeed.backend;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.ayush.newsfeed.backend.loremipsum.RandomLoremGenerator.writeNextDocument;

/**
 * Created by dexter on 07/06/2016.
 */
public class RandomLoremServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RandomLoremServlet.class.getName());

    private static final int PARA_COUNT = 3; //around 3 paragraphs
    private static final int SENTENCE_COUNT = 4; //around 4 sentences
    private static final int WORDS_COUNT = 10; //around 10 words

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setBufferSize(1024);

        resp.addHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0, proxy-revalidate, no-transform, private"); //do not cache
        resp.addHeader("Transfer-Encoding", "chunked");
        resp.setContentType("text/plain");

        LOGGER.info("Using buffer of size " + resp.getBufferSize());

        writeNextDocument(
                toIntSafe(req.getParameter("p"), PARA_COUNT),
                toIntSafe(req.getParameter("s"), SENTENCE_COUNT),
                toIntSafe(req.getParameter("w"), WORDS_COUNT),
                resp.getOutputStream());

        LOGGER.info("Used buffer of size " + resp.getBufferSize());
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