// automatically generated, do not modify

package com.ayush.newsfeed.common.models;

public final class Category {
    private Category() {
    }

    public static final byte Entertainment = 0;
    public static final byte News = 1;
    public static final byte Technology = 2;
    public static final byte Sports = 3;
    public static final byte Knowledge = 4;
    public static final byte Others = 5;

    private static final String[] names = {"Entertainment", "News", "Technology", "Sports", "Knowledge", "Others",};

    public static final int MAX = names.length;

    public static String name(int e) {
        return names[e];
    }
}
