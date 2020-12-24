package bullscows;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class for playing Bulls and Cows.
 */
public class Gameplay {
    private static final Scanner SCANNER = new Scanner(System.in);    // for user input
    private static final StringBuilder SECRET = new StringBuilder();  // secret code to guess
    private static int length;                                        // length of the secret code
    private static boolean gameOver;                                  // is game over or not

    public static void main(String[] args) {
        length = checkInput("Input the length of the secret code:");
        int range = checkInput("Input the number of possible symbols in the code.");
        checkInput(range);
        while (!gameOver) {
            play();
        }
    }

    /**
     * Gets the number input by the user, if user input is not a number ends the program.
     * @param message message to be displayed for the user
     * @return the number input
     */
    private static int checkInput(String message) {
        System.out.println(message);
        String input = SCANNER.nextLine();
        int num = 0;
        try {
            num = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.printf("Error: \"%s\" isn't a valid number.", input);
            System.exit(0);
        }
        return num;
    }

    /**
     * Checks if it is possible to initialize the game with given inputs.
     * If yes, continues, otherwise shuts down.
     */
    private static void checkInput(int range) {
        if (length > range || range <= 0 || length <= 0) {     // fail
            System.out.printf("Error: it's not possible to generate a code with a length of %d" +
                    " with %d unique symbols.", length, range);
            System.exit(0);
        } else if (range > 36) {                               // fail
            System.out.println("Error: maximum number of possible symbols in the code is 36 (0-9, a-z).");
            System.exit(0);
        }
        start(range);                                          // success
    }

    /**
     * Grades users every move based on the games rules.
     * @param guess the users guess
     * @return {@code true} if user guessed the secret, otherwise {@code false}
     */
    private static boolean grade(String guess) {
        int bulls = 0;
        int cows = 0;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < length; j++) {
                if (i == j && SECRET.charAt(i) == guess.charAt(j)) {    // bull
                    bulls++;
                } else if (SECRET.charAt(i) == guess.charAt(j)) {       // cow
                    cows++;
                }
            }
        }
        System.out.println("Grade: " + getGrade(bulls, cows));
        return bulls == length;
    }

    /**
     * Informs the user about the number of cows and/or bulls hit.
     * @param bulls the number of bulls hit
     * @param cows the number of cows hit
     * @return the result
     */
    private static String getGrade(int bulls, int cows) {
        String bullsNumber = (bulls == 1) ? "1 bull" : String.format("%d bulls", bulls);
        String cowsNumber = (cows == 1) ? "1 cow" : String.format("%d cows", cows);
        if (bulls == 0 && cows > 0) {
            return cowsNumber;
        } else if (bulls > 0 && cows == 0) {
            return bullsNumber;
        } else if (bulls > 0 && cows > 0) {
            return bullsNumber + " and " + cowsNumber;
        } else {
            return "None";
        }
    }

    /**
     * Game goes on until the user guesses the secret.
     */
    private static void play() {
        String guess;
        int turn = 1;
        while (!gameOver) {
            System.out.printf("Turn %d:\n", turn);
            do {
                guess = SCANNER.nextLine();
            } while (guess.length() != length);    // length of the guess have to be correct
            turn++;
            gameOver = grade(guess);
        }
        System.out.println("Congratulations! You guessed the secret code.");
    }

    /**
     * Initializes the game.
     * @param range the range of values used for the secret
     */
    private static void start(int range) {
        char[] code = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
        String info = " ";                               // inform the user about the range of values used
        if (range < 11) {
            info += "(0-" + code[range - 1] + ").";
        } else if (range == 11) {
            info += "(0-9, a).";
        } else {
            info += "(0-9, a-" + code[range - 1] + ").";
        }

        ArrayList<Character> list = new ArrayList<>();
        for (int i = 0; i < range; i++) {
            list.add(code[i]);
        }

        for (int i = 0; i < length; i++) {
            int random = (int) Math.floor(Math.random() * range);
            SECRET.append(list.get(random));              // append random char
            list.remove(random);                          // use each char only once
            range--;                                      // so loop works
        }

        System.out.print("The secret is prepared: ");
        int num = length;
        while (num > 0) {
            System.out.print("*");
            num--;
        }
        System.out.println(info);
        System.out.println("Okay, let's start a game!");
//        System.out.println(secret);                     // uncomment to try the program more easily
    }
}
