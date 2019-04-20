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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 7;
    private Random random = new Random();

    private ArrayList<String> wordList=new ArrayList<>();

    //create two new data structures:
    //rapidly (in O(1)) verify whether a word is valid.
    private HashSet<String> wordSet=new HashSet<>();
    //sortLetters version of a string as the key, an ArrayList of the words that correspond to that key as our value
    private HashMap<String, ArrayList<String>> lettersToWord=new HashMap<>(); //key: "opst" value: ["post", "spot", "pots", "tops", ...]

    //in addition to populating wordList,
    //you should also store each word in a HashMap (let's call it sizeToWords)
    private HashMap<Integer, ArrayList<String>> sizeToWords=new HashMap<>();

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;
        while((line = in.readLine()) != null) {
            String word = line.trim();

            //Each word that is read from the dictionary file should be stored in an ArrayList (called wordList);
            wordList.add(word);
            wordSet.add(word);
            //check whether lettersToWord already contains an entry for that key.
            String sortWord=sortLetters(word);
            //If it does, add the current word to ArrayList at that key.
            if(lettersToWord.containsKey(sortWord))
                lettersToWord.get(sortWord).add(word);
            //Otherwise, create a new ArrayList, add the word to it and store in the HashMap with the corresponding key.
            else
                lettersToWord.put(sortWord, new ArrayList<String>());

            //that maps word length to an ArrayList of all words of that length.
            // This means, for example, you should be able to get all four-letter words in the dictionary by calling sizeToWords.get(4).
            if(sizeToWords.containsKey(word.length()))
                sizeToWords.get(word.length()).add(word);
            else
                sizeToWords.put(word.length(), new ArrayList<String>());
        }
    }

    //takes a string and finds all the anagrams
    //compare each string in wordList to the input word to determine if they are anagrams
    public List<String> getAnagrams(String targetWord) {    //targetWord="STOP"
        ArrayList<String> result = new ArrayList<String>();
        String sortWord=sortLetters(targetWord);    //sortWord="opst"

        for(String s: wordList){
            //checking that they are the same length (for the sake of speed)
            // and checking that the sorted versions of their letters are equal.
            if(s.length()==sortWord.length() && sortLetters(s).equalsIgnoreCase(sortWord));//constructor did not sorted so need to sort again
                result.add(targetWord);
        }
        return result;
    }

    //check whether lettersToWord already contains an entry for that key.
    //If it does, add the current word to ArrayList at that key
    //Otherwise, create a new ArrayList, add the word to it and store in the HashMap with the corresponding key.
    private String sortLetters(String s){
        char[] wordArr=s.toCharArray();
        Arrays.sort(wordArr);
        return new String(wordArr);
    }

    //check:
    //1. the provided word is a valid dictionary word (i.e., in wordSet), and
    //2. the word does not contain the base word as a substring.
    public boolean isGoodWord(String word, String base) {
        String sortWord=sortLetters(word);
        for(String s: wordSet)
            if(!sortWord.equalsIgnoreCase(s) && word.contains(base))
                return false;
        return true;
    }

    //takes a string, and finds all anagrams that can be formed by adding one letter to that word
    //instantiate a new ArrayList as your return value,
    //then check the given word + each letter of the alphabet one by one against the entries in lettersToWord.
    //update defaultAction in AnagramActivity to invoke getAnagramsWithOneMoreLetter instead of getAnagrams.
    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();

        for(String s:wordList){
            for(char c='a'; c<'z'; c++){
                String newWord=word+c;
                String sortWord=sortLetters(newWord);

                ArrayList<String> wordsInDic=lettersToWord.get(sortWord);
                if(wordsInDic!=null)
                    result.addAll(wordsInDic);
            }
        }
        return result;
    }

    //Pick a random starting point in the wordList array
    //check each word in the array until you find one that has at least MIN_NUM_ANAGRAMS anagrams
    //Be sure to handle wrapping around to the start of the array if needed.
    public String pickGoodStarterWord() {
        String goodWord="";
        int listSize=wordList.size();
        int anyNumber = random.nextInt(listSize - 1);

        //create a new member variable called wordLength and default it to DEFAULT_WORD_LENGTH.
        int wordLength=DEFAULT_WORD_LENGTH;
        //Then in pickGoodStarterWord, restrict your search to the words of length wordLength,
        for(int i=anyNumber; (i+listSize)%listSize<listSize && wordLength<=MAX_WORD_LENGTH; i++){
            String word = wordList.get(i);
            String sortWord = sortLetters(word);
            ArrayList<String> goodWordList = lettersToWord.get(sortWord);
            if (goodWordList.size() >= MIN_NUM_ANAGRAMS || word.length()>=wordLength) {
                goodWord = word;
                break;
            }
            //increment wordLength (unless it's already at MAX_WORD_LENGTH)
            //so that the next invocation will return a larger word.
            wordLength++;
        }



        return goodWord;
    }
}
