package com.ayush.newsfeed.common;

import java.util.Random;

/**
 * Created by dexter on 14/06/2016.
 */
public class RandomIterator {

    private static final byte INFINITE = -1;

    private final Random random = new Random();

    private final int[] offsets;
    private final int iterationLimit;

    public RandomIterator(int wordCount, int iterationLimit) {

        this.offsets = new int[currentMax = wordCount];
        this.iterationLimit = iterationLimit;

        for (int index = 0; index < wordCount; index++)
            offsets[index] = index;
    }

    public RandomIterator(int wordCount) {

        this.offsets = new int[currentMax = wordCount];
        this.iterationLimit = INFINITE;

        for (int index = 0; index < wordCount; index++)
            offsets[index] = index;
    }

    private int count;
    private int currentMax;

    private int getRandomIndex() {

        final int randomIndex = random.nextInt(currentMax);
        final int toReturn = offsets[randomIndex];

        currentMax--;

        if (currentMax < 1)
            currentMax = offsets.length; //reset
        else
            swap(randomIndex, currentMax);

        return toReturn;
    }

    private void swap(int a, int b) {

        final int hold = offsets[a];
        offsets[a] = offsets[b];
        offsets[b] = hold;
    }

    public boolean hasNext() {
        return iterationLimit == INFINITE || count < iterationLimit;
    }

    public int nextIndex() {

        count++;
        return getRandomIndex();
    }
}