package com.ayush.newsfeed.backend.loremipsum;

import com.ayush.newsfeed.common.RandomIterator;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Consider weighted randomness which reduces the probability of previously chosen words
 * http://stackoverflow.com/questions/6409652/random-weighted-selection-in-java
 * primitive array based replacement for map should give decent performance since not many words
 * Created by dexter on 14/06/2016.
 */
public enum RandomLoremGenerator {

    ;
    private static final byte COMMA = ',';
    private static final byte EXCLAMATION = '!';
    private static final byte FULL_STOP = '.';
    private static final byte SPACE = ' ';
    private static final byte NEXT_LINE = '\n';

    private static final byte[][] DICTIONARY_1 = Dictionary_1.FAST_WORDS;
    private static final byte[][] DICTIONARY_2 = Dictionary_2.FAST_WORDS;

    private static final int TOTAL_LENGTH = DICTIONARY_1.length + DICTIONARY_2.length;

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final RandomIterator RANDOM_INDEX_ITERATOR = new RandomIterator(TOTAL_LENGTH);

    private static boolean shouldPutComma() {
        return RANDOM.nextInt(15) == 1;
    }

    private static boolean shouldPutExclamation() {
        return RANDOM.nextInt(30) == 1;
    }

    private static byte[] nextWord() {

        final int index = RANDOM_INDEX_ITERATOR.nextIndex();
        if (index < DICTIONARY_1.length)
            return DICTIONARY_1[index];
        else
            return DICTIONARY_2[index - DICTIONARY_1.length];
    }

    public static void writeNextWord(OutputStream outputStream) throws IOException {
        outputStream.write(nextWord());
    }

    public static void writeNextSentence(int words, OutputStream outputStream) throws IOException {

        words = RANDOM.nextInt((words * 3) / 4, (words * 5) / 4);
        words = words == 0 ? 1 : words;

        //capitalize first word and write-out
        final byte[] firstWord = nextWord();
        outputStream.write(Character.toUpperCase(firstWord[0]));
        outputStream.write(firstWord, 1, firstWord.length - 1);

        if (words > 1) {

            outputStream.write(SPACE);
            for (int index = 1; index < words - 1; index++) {

                writeNextWord(outputStream);

                if (shouldPutComma())
                    outputStream.write(COMMA);

                outputStream.write(SPACE);
            }

            //write last word
            writeNextWord(outputStream);
        }

        //end the sentence
        outputStream.write(shouldPutExclamation() ? EXCLAMATION : FULL_STOP);
    }

    /**
     * @param sentences    around these sentences
     * @param words        around these words
     * @param outputStream write-out to this stream
     */
    public static void writeNextParagraph(int sentences, int words, OutputStream outputStream) throws IOException {

        sentences = RANDOM.nextInt((sentences * 3) / 4, (sentences * 5) / 4);
        sentences = sentences == 0 ? 1 : sentences;

        for (int index = 0; index < sentences - 1; index++) {

            writeNextSentence(words, outputStream);
            outputStream.write(SPACE);
        }

        //write last sentence
        writeNextSentence(words, outputStream);
    }

    public static void writeNextDocument(int paragraphs, int sentences, int words, OutputStream outputStream) throws IOException {

        paragraphs = RANDOM.nextInt((paragraphs * 3) / 4, (paragraphs * 5) / 4);
        paragraphs = paragraphs == 0 ? 1 : paragraphs;

        for (int index = 0; index < paragraphs - 1; index++) {

            writeNextParagraph(sentences, words, outputStream);
            outputStream.write(NEXT_LINE);
        }

        //write last paragraph
        writeNextParagraph(sentences, words, outputStream);
    }
}