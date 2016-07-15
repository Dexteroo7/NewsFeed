//package com.ayush.newsfeed.backend;
//
//import com.ayush.newsfeed.backend.loremipsum.Dictionary_1;
//import com.ayush.newsfeed.common.RandomIterator;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * Created by dexter on 13/06/2016.
// */
//public class TestGeneration {
//
//    public static void main(String[] args) {
//
//        final RandomIterator<byte[]> iterator = new RandomIterator<>(Dictionary_1.FAST_WORDS);
//
//        final Set<byte[]> checker = new HashSet<>(Dictionary_1.FAST_WORDS.length);
//        int counter = 0;
//
//        while (iterator.hasNext()) {
//
//            final byte[] word = iterator.next();
//            if (!checker.add(word))
//                System.out.println("Duplicate found");
//
//            counter++;
//            System.out.println(new String(word));
//        }
//
//        System.out.println(checker.size() + " " + Dictionary_1.FAST_WORDS.length + " " + counter);
//    }
//}