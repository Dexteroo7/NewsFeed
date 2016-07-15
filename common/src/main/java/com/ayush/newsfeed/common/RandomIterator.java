package com.ayush.newsfeed.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by dexter on 14/06/2016.
 */
public class RandomIterator<T> implements Iterator<T> {

    protected final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final LinkedList<Integer> offsets = new LinkedList<>();
    private final T[] words;

    public RandomIterator(T[] words) {

        this.words = words;

        for (int index = 0; index < words.length; index++)
            offsets.add(index);
    }

    public RandomIterator(Collection<T> words) {

        //noinspection unchecked
        this.words = (T[]) words.toArray();

        for (int index = 0; index < this.words.length; index++)
            offsets.add(index);
    }

    @Override
    public boolean hasNext() {
        return !offsets.isEmpty();
    }

    private int getRandomIndex() {
        return random.nextInt(0, offsets.size());
    }

    @Override
    public T next() {

        final int index = offsets.remove(getRandomIndex());
        return words[index];
    }
}