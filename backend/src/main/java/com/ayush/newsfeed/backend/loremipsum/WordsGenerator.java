//package com.ayush.newsfeed.backend.loremipsum;
//
//import java.io.IOException;
//import java.io.RandomAccessFile;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * Created by dexter on 12/06/2016.
// */
//public class WordsGenerator {
//
//    public static void main(String[] args) throws IOException {
//
//        System.out.println(Dictionary_1.FAST_WORDS.length);
//        System.out.println(Dictionary_2.FAST_WORDS.length);
//
//        final Map<Character, Integer> letterCounter = new HashMap<>();
//        final List<String> allWords = Files.readAllLines(Paths.get("/Users/dexter/Downloads/Latin_lower"));
//        final RandomAccessFile array1 = new RandomAccessFile("/Users/dexter/Desktop/array_1", "rw");
//        final RandomAccessFile array2 = new RandomAccessFile("/Users/dexter/Desktop/array_2", "rw");
//
//        array1.setLength(0);
//        array2.setLength(0);
//
//        final AtomicInteger counter1 = new AtomicInteger(0);
//        final AtomicInteger counter2 = new AtomicInteger(0);
//        int length, index;
//        boolean fileSwitch = false;
//
//        for (String word : allWords) {
//
//            //check illegal
//            if (isEmpty(word) || !isLettersOnly(word) || word.length() < 3)
//                continue;
//
//            final char firstLetter = word.charAt(0);
//
//            //max 100 words for each alphabet
//            final int currentCount = letterCounter.getOrDefault(firstLetter, 0);
//            if (currentCount > 100)
//                continue;
//            else
//                letterCounter.put(firstLetter, currentCount + 1);
//
//            //keep switching the files
//            final RandomAccessFile file;
//            final AtomicInteger counter;
//
//            if (fileSwitch = !fileSwitch) {
//                file = array1;
//                counter = counter1;
//            } else {
//                file = array2;
//                counter = counter2;
//            }
//
//            file.writeChar('{');
//
//            file.writeUTF((byte) firstLetter + "");
//            length = word.length();
//            for (index = 1; index < length; index++) {
//                file.writeChar(',');
//                file.writeUTF((byte) word.charAt(index) + "");
//            }
//
//            file.writeChar('}');
//            file.writeChar(',');
//
//            final int counterValue = counter.incrementAndGet();
//            if (counterValue % 400 == 0) {
//
//                System.out.println(counterValue);
//                file.writeChar('\n');
//            }
//        }
//
//        array1.close();
//        array2.close();
//    }
//
//    public static boolean isEmpty(CharSequence sequence) {
//        return sequence == null || sequence.length() == 0;
//    }
//
//    public static boolean isLettersOnly(String toCheck) {
//
//        int length = toCheck.length();
//        for (int index = 0; index < length; index++)
//            if (!Character.isAlphabetic(toCheck.charAt(index)))
//                return false;
//
//        return true;
//    }
//}
