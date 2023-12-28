// Peter Le 
// 10/30/23
// CSE 122
// P2: Absurdle
// TA: Jasmine Herri
//
// This class is the game Absurdle. It takes in the user's desired dictionary and word length.
// As well as guesses to lengthen the game depending on the users guess.
import java.util.*;
import java.io.*;

public class Absurdle  {
    public static final String GREEN = "🟩";
    public static final String YELLOW = "🟨";
    public static final String GRAY = "⬜";

    
    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.println("Welcome to the game of Absurdle.");

        System.out.print("What dictionary would you like to use? ");
        String dictName = console.next();

        System.out.print("What length word would you like to guess? ");
        int wordLength = console.nextInt();

        List<String> contents = loadFile(new Scanner(new File(dictName)));
        Set<String> words = pruneDictionary(contents, wordLength);

        List<String> guessedPatterns = new ArrayList<>();
        while (!isFinished(guessedPatterns)) {
            System.out.print("> ");
            String guess = console.next();
            String pattern = record(guess, words, wordLength);
            guessedPatterns.add(pattern);
            System.out.println(": " + pattern);
            System.out.println();
        }
        System.out.println("Absurdle " + guessedPatterns.size() + "/∞");
        System.out.println();
        printPatterns(guessedPatterns);
    }


    // Prints out the given list of patterns.
    // - List<String> patterns: list of patterns from the game
    public static void printPatterns(List<String> patterns) {
        for (String pattern : patterns) {
            System.out.println(pattern);
        }
    }

    // Returns true if the game is finished, meaning the user guessed the word. Returns
    // false otherwise.
    // - List<String> patterns: list of patterns from the game
    public static boolean isFinished(List<String> patterns) {
        if (patterns.isEmpty()) {
            return false;
        }
        String lastPattern = patterns.get(patterns.size() - 1);
        return !lastPattern.contains("⬜") && !lastPattern.contains("🟨");
    }

   
    // Loads the contents of a given file Scanner into a List<String> and returns it.
    // - Scanner dictScan: contains file contents
    public static List<String> loadFile(Scanner dictScan) {
        List<String> contents = new ArrayList<>();
        while (dictScan.hasNext()) {
            contents.add(dictScan.next());
        }
        return contents;
    }

     
    // Tailors the users selected dictionary depending on length of words
    // Parameters:
    //  - contents: A list that contains all of the words in the selected dictionary
    //  - wordLength: Provided by the user and the word length they choose
    // Exceptions:
    //  - Throws an IllegalArgumentException when the word length is less than one
    // Returns: words that fit the users desired wordLength
    public static Set<String> pruneDictionary(List<String> contents, int wordLength) {
        if(wordLength < 1) {
            throw new IllegalArgumentException();
        }
        Set<String> dictionary = new TreeSet<>();
        for(String word : contents) {
            if(word.length() == wordLength) {
                dictionary.add(word);
            }
        }
        // takes the contents and user word length, return a set<String> 
        // containing only the words from the dict and length specified
        return dictionary;
    }

    // Keeps track of the users guesses and considers the
    // Possible patterns that it can still choose from.
    // As well as chooses the pattern that has the largest set of words still remaning.
    // Updates the current set of words based on the guess to be the selected largest set. 
    // Parameters: 
    //  - guess: User's guess
    //  - words: records each pattern 
    //  - wordLength: User's desired word Length
    // Exceptions:
    //  - Throws an IllegalArgumentException when the set of words is empty 
    //  - or when the guess isn't the same length as the words.
    // Returns: 
    //  - bestPattern: The set of words with the largest amount of word choices.
    public static String record(String guess, Set<String> words, int wordLength) {
        if(words.isEmpty() || guess.length() != wordLength) {
            throw new IllegalArgumentException();
        }
        String words2 = words + "";
        String biggest = "";
        Map<String, Set<String>> keepTrack = new TreeMap<>();
        for(String word : words) {
            String pattern = patternFor(word, guess);
             if(!keepTrack.containsKey(pattern)) {
                Set<String> values = new TreeSet<>();
                values.add(word);
                keepTrack.put(pattern, values);
            }   else {
                Set<String> getValues = keepTrack.get(pattern);
                getValues.add(word);
            }
        }
        String bestPattern = "";
        Set<String> bestWords = new HashSet<>();
            for (Map.Entry<String, Set<String>> entry : keepTrack.entrySet()) {
                if (entry.getValue().size() > bestWords.size()) {
                    bestPattern = entry.getKey();
                    bestWords = entry.getValue();
                }
        }
        helper(words, guess, bestPattern);
        return bestPattern;
    }

    // Checks to see and add the bestPattern
    // Parameters:
    //  - words: contains all of the patterns
    //  - guess: User's guess
    //  - bestPattern: the pattern with the largest patterns avaliable
    public static void helper(Set<String> words, String guess, String bestPattern) {
        Set<String> nextWords = new HashSet<>();
        for (String word : words) {
            String yes = patternFor(word, guess);
            if (yes.equals(bestPattern)) {
                nextWords.add(word);
            }
        }
        words.retainAll(nextWords);
    }



    // Produces a pattern from the user's given guesses
    // Parameters:
    //  - word: A string of words within the dictionary
    //  - guess: User's guesses
    // Returns: 
    //  - pattern: contains the pattern for the guess and word
    public static String patternFor(String word, String guess) {
        List<String> guessList = new ArrayList<>();
        Map<Character, Integer> countWords = new TreeMap<>();
        String pattern = "";
        for(int i = 0; i < word.length(); i++) {
            char wordChar = word.charAt(i);
            char guessChar = guess.charAt(i);
            guessList.add(guessChar + "");
            if(!countWords.containsKey(wordChar)) {
                countWords.put(wordChar, 1);
            } else {
                countWords.put(wordChar, countWords.get(wordChar) + 1);
            }
        }

        for(int j = 0; j < guess.length(); j++) {
            char charGuess = guess.charAt(j);
            if(charGuess == (word.charAt(j)) && countWords.get(charGuess) 
                > 0 && countWords.containsKey(charGuess)) {
                guessList.add(j, GREEN);
                guessList.remove(j + 1);
                countWords.put(charGuess, countWords.get(charGuess) - 1);
            }
        }

        for(int i = 0; i < guess.length(); i++) {
            char givenGuess = guess.charAt(i);
            if (countWords.containsKey(givenGuess) && countWords.get(givenGuess)
                    > 0 && guessList.get(i) != GREEN) {
                guessList.add(i, YELLOW);
                guessList.remove(i + 1);
                countWords.put(givenGuess, countWords.get(givenGuess) - 1);
            } 
            
            
        }

        for(int l = 0; l < guess.length(); l++) {
            char givenGuess = word.charAt(l);
            if(guessList.get(l) != GREEN && guessList.get(l) != YELLOW) {
                guessList.add(l, GRAY);
                guessList.remove(l + 1);
                countWords.put(givenGuess, countWords.get(givenGuess) - 1);
            }
        }

        for(int i = 0; i < guessList.size(); i++) {
            pattern += guessList.get(i);
        }
        return pattern;
    }
}
