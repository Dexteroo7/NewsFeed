//package com.ayush.newsfeed.backend.loremipsum;
//
//import com.ayush.newsfeed.common.RandomIterator;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * Created by dexter on 20/07/2016.
// */
//public class RandomTest {
//
//
//    public static void main(String[] args) {
//
//        final Set<Integer> duplicateChecker = new HashSet<>();
//        final RandomIterator randomIterator = new RandomIterator(1000000);
//
//        for (int index = 0; index < 10000; index++) {
//
//            final int randomIndex = randomIterator.nextIndex();
//            final boolean result = duplicateChecker.add(randomIndex);
//            if (!result)
//                System.out.println("Duplicate found " + randomIndex);
//        }
//    }
//}
