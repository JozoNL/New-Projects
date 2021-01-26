package flashcards;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.StringJoiner;

/**
 * Class for managing flashcards.
 */
public class Flashcard {
    private static final Scanner SCANNER = new Scanner(System.in);     // for user input
    private static final ArrayList<Card> CARDS = new ArrayList<>();    // list of cards

    public static void main(String[] args) {
        manage();
    }

    /**
     * Manages the work with flashcards until exit command is chosen.
     */
    private static void manage() {
        String input = "";
        while (!input.equals("exit")) {
            System.out.println("\nInput the action (add, remove, " +
                               "import, export, ask, hardest card, reset stats, exit):");
            input = SCANNER.nextLine();
            process(input);
        }
    }

    /**
     * Processes the user input accordingly.
     * @param option option chosen by the user
     */
    private static void process(String option) {
        switch (option) {
            case "add" -> add();
            case "remove" -> remove();
            case "import" -> load();
            case "export" -> save();
            case "ask" -> ask();
            case "hardest card" -> hardestCard();
            case "reset stats" -> resetStats();
            case "exit" -> System.out.println("Bye!");
            default -> System.out.println("Wrong input. Try again.");
        }
    }

    /**
     * Adds the card if it contains unique values, otherwise does nothing.
     */
    private static void add() {
        System.out.println("The card:");
        String term = SCANNER.nextLine();
        if (containsTerm(term)) {                          // only unique terms allowed
            System.out.printf("The card \"%s\" already exists.\n", term);
            return;
        }
        System.out.println("The definition of the card:");
        String definition = SCANNER.nextLine();
        if (containsDefinition(definition)) {              // only unique definitions allowed
            System.out.printf("The definition \"%s\" already exists.\n", definition);
            return;
        }
        CARDS.add(new Card(term, definition, 0));  // success
        System.out.printf("The pair (\"%s\":\"%s\") has been added.\n", term, definition);
    }

    /**
     * Removes the chosen card if it exists.
     */
    private static void remove() {
        System.out.println("Which card?");
        String card = SCANNER.nextLine();
        boolean removed = CARDS.removeIf(c -> c.term.equals(card));
        System.out.printf((removed) ? "The card has been removed.\n"
                                    : "Can't remove \"%s\": there is no such card.\n", card);
    }

    /**
     * Saves the cards to the specified file.
     */
    private static void save() {
        int counter = 0;
        System.out.println("File name:");
        String fileName = SCANNER.nextLine();
        try (PrintWriter writer = new PrintWriter(fileName)) {
            for (Card card : CARDS) {            // each card takes 3 lines
                writer.println(card.term);       // first line (term)
                writer.println(card.definition); // second line (definition)
                writer.println(card.mistakes);   // third line (mistakes)
                counter++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
        System.out.printf((counter == 1) ? "1 card has been saved.\n" : "%d cards have been saved.\n", counter);
    }

    /**
     * Loads the cards from the specified file. If the same card is encountered,
     * values for definition and mistakes are overwritten.
     */
    private static void load() {
        int counter = 0;
        System.out.println("File name:");
        String fileName = SCANNER.nextLine();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while (reader.ready()) {
                String term = reader.readLine();
                String definition = reader.readLine();
                int mistakes = Integer.parseInt(reader.readLine());
                if (containsTerm(term)) {                              // overwrite
                    Card card = getCard(term);
                    card.definition = definition;
                    card.mistakes = mistakes;
                } else {
                    CARDS.add(new Card(term, definition, mistakes));   // or make new
                }
                counter++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("Some I/O error has occurred.");
        }
        System.out.printf((counter == 1) ? "1 card has been loaded.\n" : "%d cards have been loaded.\n", counter);
    }

    /**
     * Tests users knowledge of the flashcards' definitions.
     */
    private static void ask() {
        if (CARDS.isEmpty()) {
            System.out.println("You have not added any flashcards.");
            return;
        }
        System.out.println("How many times to ask?");
        int times;
        try {
            times = Integer.parseInt(SCANNER.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Wrong input.");
            return;
        }
        Random random = new Random();
        for (; times > 0; times--) {
            int num = random.nextInt(CARDS.size());    // get random number based on list's size
            System.out.printf("Print the definition of \"%s\":\n", CARDS.get(num).term);
            String right = CARDS.get(num).definition;  // right answer
            String answer = SCANNER.nextLine();        // user's answer
            if (right.equals(answer)) {                // if equal
                System.out.println("Correct.");
            } else if (containsDefinition(answer)) {   // if wrong answer but fits some other card
                System.out.printf("Wrong. The right answer is \"%s\", but your definition " +
                                  "is correct for \"%s\".\n", right, defineTerm(answer));
                CARDS.get(num).mistakes++;
            } else {                                   // if answer doesn't fit any card
                System.out.printf("Wrong. The right answer is \"%s\".\n", right);
                CARDS.get(num).mistakes++;
            }
        }
    }

    /**
     * Looks whether the definition exists for some card.
     * @param definition definition to search for
     * @return {@code true} in case the definition exists, otherwise {@code false}
     */
    private static boolean containsDefinition(String definition) {
        for (Card card : CARDS) {
            if (card.definition.equals(definition))
                return true;
        }
        return false;
    }

    /**
     * Looks whether the term exists for some card.
     * @param term term to search for
     * @return {@code true} in case the term exists, otherwise {@code false}
     */
    private static boolean containsTerm(String term) {
        for (Card card : CARDS) {
            if (card.term.equals(term))
                return true;
        }
        return false;
    }

    /**
     * Gets the term for the specific definition.
     * @param definition definition for this card
     * @return term for this card
     */
    private static String defineTerm(String definition) {
        for (Card card : CARDS) {
            if (card.definition.equals(definition))
                return card.term;
        }
        return "";                                            // won't get here
    }

    /**
     * Finds the card with the specific term.
     * @param term term to search for
     * @return the card with the specific term
     */
    private static Card getCard(String term) {
        for (Card card : CARDS) {
            if (card.term.equals(term))
                return card;
        }
        return new Card("", "", 0);     // won't get here
    }

    /**
     * Finds and prints the card or cards with the highest number of errors.
     */
    private static void hardestCard() {
        int max = 0;
        for (Card card : CARDS) {                                     // find max number of mistakes
            if (card.mistakes > max)
                max = card.mistakes;
        }
        if (max == 0) {                                               // if no mistakes
            System.out.println("There are no cards with errors.");
            return;
        }
        StringJoiner joiner = new StringJoiner(", ");         // else
        int counter = 0;
        for (Card card : CARDS) {                                     // find all cards with max number of mistakes
            if (card.mistakes == max) {
                joiner.add(String.format("\"%s\"", card.term));
                counter++;                                            // count them
            }
        }
        System.out.printf((counter == 1) ? "The hardest card is %s. "
                                         : "The hardest cards are %s. ", joiner.toString());

        System.out.printf((counter == 1) ? (max == 1) ? "You have %d error answering it.\n"
                                                      : "You have %d errors answering it.\n"
                                         : (max == 1) ? "You have %d error answering them.\n"
                                                      : "You have %d errors answering them.\n", max);
    }

    /**
     * Sets number of mistakes for all cards to 0.
     */
    private static void resetStats() {
        for (Card card : CARDS)
            card.mistakes = 0;
        System.out.println("Card statistics have been reset.");
    }

    /**
     * Class representing each individual card.
     */
    private static class Card {
        private final String term;              // unique term for the card (front side)
        private String definition;              // unique definition for the card (back side)
        private int mistakes;                   // number of mistakes made answering the card

        /**
         * Constructor for the card.
         * @param term card's term
         * @param definition card's definition
         * @param mistakes number of mistakes
         */
        private Card(String term, String definition, int mistakes) {
            this.term = term;
            this.definition = definition;
            this.mistakes = mistakes;
        }
    }
}
