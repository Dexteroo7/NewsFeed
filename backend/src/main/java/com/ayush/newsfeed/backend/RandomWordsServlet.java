package com.ayush.newsfeed.backend;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.ayush.newsfeed.backend.loremipsum.RandomLoremGenerator.writeNextWord;

/**
 * Created by dexter on 20/07/2016.
 */
public class RandomWordsServlet extends HttpServlet {

//    private static final Logger LOGGER = Logger.getLogger(RandomLoremServlet.class.getName());
    private static final int WORDS_COUNT = 100; // 100 words
    private static final byte NEXT_LINE = '\n';

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.addHeader("Cache-Control", "no-cache, no-store, must-revalidate, max-age=0, proxy-revalidate, no-transform, private"); //do not cache
        resp.setContentType("text/plain");

        final int wordCount = toIntSafe(req.getParameter("w"), WORDS_COUNT);
        final OutputStream stream = resp.getOutputStream();

        writeNextWord(stream);
        for (int index = 1; index < wordCount; index++) {
            stream.write(NEXT_LINE);
            writeNextWord(stream);
        }
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
