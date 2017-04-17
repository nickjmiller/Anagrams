/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private HashSet wordSet;
    private HashMap<String,ArrayList<String>> lettersToWord;
    private HashMap<Integer,ArrayList<String>> sizeToWords;
    private Random random = new Random();
    private int wordLength;

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        wordSet = new HashSet();
        lettersToWord = new HashMap<>();
        sizeToWords = new HashMap<>();
        wordLength = DEFAULT_WORD_LENGTH;

        while((line = in.readLine()) != null) {
            String word = line.trim();
            String key = sortLetters(word);
            int size = word.length();

            if (lettersToWord.containsKey(key)) {
                lettersToWord.get(key).add(word);
            } else {
                ArrayList<String> tempList = new ArrayList<>();
                tempList.add(word);
                lettersToWord.put(key, tempList);
            }

            if (sizeToWords.containsKey(size)) {
                sizeToWords.get(size).add(word);
            } else {
                ArrayList<String> tempList = new ArrayList<>();
                tempList.add(word);
                sizeToWords.put(size, tempList);
            }
            wordSet.add(word);
        }
        cullLists();
    }

    public boolean isGoodWord(String word, String base) {
        if (!wordSet.contains(word)) {
            return false;
        }
        return (!word.contains(base));
    }



    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for (char letter = 'a'; letter <= 'z'; letter++) {
            String wordNew = word + letter;
            if (lettersToWord.containsKey(sortLetters(wordNew))) {
                ArrayList<String> list = lettersToWord.get(sortLetters(wordNew));
                for (String anagram:list
                     ) {
                    if (!anagram.contains(word)) {
                        result.add(anagram);
                    }

                }
            }
        }
        return result;
    }
    //double check this for + sec_letter
    public List<String> getAnagramsWithTwoMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for (char letter = 'a'; letter <= 'z'; letter++) {
            String wordNew = word + letter;
            for (char sec_letter = letter; sec_letter <= 'z'; sec_letter++) {
                if (lettersToWord.containsKey(sortLetters(wordNew + sec_letter))) {
                    ArrayList<String> list = lettersToWord.get(sortLetters(wordNew + sec_letter));
                    for (String anagram:list
                            ) {
                        if (!anagram.contains(word)) {
                            result.add(anagram);
                        }

                    }
                }
            }
        }
        return result;
    }

    public String pickGoodStarterWord() {
        ArrayList<String> list;
        list = sizeToWords.get(wordLength);
        int i = random.nextInt(list.size());
        String word = list.get(i);
        if (wordLength < MAX_WORD_LENGTH) {
            wordLength++;
        }

        return word;
    }


    public void resetLength(){
        wordLength = DEFAULT_WORD_LENGTH;
    }

    private String sortLetters (String string) {
        char[] chars = string.toCharArray();
        Arrays.sort(chars);
        return String.valueOf(chars);
    }

    // Put this somewhere else
    private void cullLists (){
        for (int i = DEFAULT_WORD_LENGTH; i <= MAX_WORD_LENGTH; i++) {
            ArrayList<String> list = sizeToWords.get(i);
            ArrayList<String> newList = new ArrayList<>();
            for (String word:list
                 ) {
                if (getAnagramsWithOneMoreLetter(word).size() >= MIN_NUM_ANAGRAMS &&
                        getAnagramsWithTwoMoreLetter(word).size() >= MIN_NUM_ANAGRAMS) {
                    newList.add(word);
                }

            }
            sizeToWords.put(i,newList);
        }
    }
}

