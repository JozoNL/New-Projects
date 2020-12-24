package hangman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Class for playing hangman.
 */
public class Hangman {
    private static final Scanner SCANNER = new Scanner(System.in);        // for user input
    private final ArrayList<Character> wrongGuesses = new ArrayList<>();  // list of incorrect guesses
    private String word;                                                  // the word to guess
    private char[] solution;                                              // array with solution unfolding
    private int wrong;                                                    // number of wrong guesses

    public static void main(String[] args) {
        Hangman hangman = new Hangman();
        boolean valid = false;
        while (!valid)
            valid = hangman.validate();
        for (int i = 0; i < 50; i++)
            System.out.println();
        hangman.solution = hangman.word.toCharArray();
        Arrays.fill(hangman.solution, '_');
        hangman.guess();
    }

    /**
     * Prints the current state of the game based on amount of wrong guesses.
     * @param wrong the amount of wrong guesses
     */
    private void draw(int wrong) {
        System.out.println("     ___________   ");
        System.out.println("     |         |   ");
        if (wrong == 0) {
            System.out.println("               |   ");
        } else if (wrong == 1 || wrong == 2) {
            System.out.println("     O         |   ");
        } else if (wrong == 3) {
            System.out.println("     O/        |   ");
        } else {
            System.out.println("    \\O/        |   ");
        }
        System.out.println((wrong >= 2) ? "     |         |   "
                                        : "               |   ");
        if (wrong == 5) {
            System.out.println("      \\        |   ");
        } else if (wrong == 6) {
            System.out.println("    / \\        |   ");
        } else {
            System.out.println("               |   ");
        }
        System.out.println("               |   ");
        System.out.println("               |   ");
        System.out.println("            ___|___");
    }

    /**
     * Prompts the user for a guess. Processes only legal guesses.
     */
    private void guess() {
        System.out.println(solution);
        if (!wrongGuesses.isEmpty()) {                                    // if any wrong guesses
            System.out.print("Wrong guesses: ");
            wrongGuesses.forEach(e -> System.out.print(e + " "));         // display them
            System.out.println();
        }
        String letter = "";
        boolean valid = false;
        while (!valid) {
            System.out.println("Guess the letter (a-z)");                 // expects only lowercase letters
            letter = SCANNER.nextLine();
            if (letter.length() == 1 && letter.charAt(0) >= 97 && letter.charAt(0) <= 122)
                valid = true;
            else
                System.out.println("You are not in the range, try again!");
        }

        int hit = 0;                                   // how many letters you hit
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter.charAt(0)) {  // if any hit
                solution[i] = letter.charAt(0);        // reveal it
                hit++;
            }
        }
        if (hit == 0) {                                // if no hit
            wrongGuesses.add(letter.charAt(0));        // grow list of wrong guesses
            wrong++;
        }
        draw(wrong);
        if (gameOver())
            return;
        guess();
    }

    /**
     * Validates that the user input is legal.
     * @return {@code true} if user input correct word, otherwise {@code false}
     */
    private boolean validate() {
        System.out.println("Enter the word to guess (lowercase, 4-10 characters)");
        String input = SCANNER.nextLine();
        if (input.length() < 4 || input.length() > 10) {      // checks length
            System.out.println("Wrong length, try again!");
            return false;
        }
        for (char c : input.toCharArray()) {
            if (c < 97 || c > 122) {                          // checks content
                System.out.println("Invalid characters used, try again!");
                return false;
            }
        }
        word = input;                                         // success
        return true;
    }

    /**
     * Determines if the game has ended and if so, if the player has won or lost.
     * @return {@code true} if the game is over, otherwise {@code false}
     */
    private boolean gameOver() {
        if (wrong == 6) {                                          // the whole hangman was drawn
            System.out.println("Game over! :(");
            System.out.printf("The correct word was %s.", word);   // display the correct word
            return true;
        }
        if (!new String(solution).contains("_")) {                 // solution does not contain any underscore (_)
            System.out.println(word);
            System.out.println("You won! Congratulations! :)");
            return true;
        }
        return false;                                              // if either
    }
}
