package tictactoe;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Class for playing the game.
 */
public class Gameplay {
    public static void main(String[] args) {
        TicTacToe.printField();
        TicTacToe.play();
    }
}

/**
 * Class for creating the TicTacToe game.
 */
class TicTacToe {
    private static final char[][] FIELD = initField();               // playing field
    private static final Scanner SCANNER = new Scanner(System.in);   // for user input
    private static int move = 1;                                     // which move is this, 1st at the start, max 9th

    /**
     * Initializes the playing field.
     * @return the empty playing field
     */
    private static char[][] initField() {
        char[][] field = new char[3][3];       // 3x3 playing field
        for (char[] row : field) {
            Arrays.fill(row, ' ');         // fill each row (3x ' ')
        }
        return field;
    }

    /**
     * Prints the current state of the game.
     */
    static void printField() {
        System.out.println("---------");                   // upper border
        for (char[] row : FIELD) {
            for (int j = 0; j < row.length; j++) {
                if (j == 0) {
                    System.out.printf("| %c", row[j]);     // left border + first column
                } else if (j == 1) {
                    System.out.printf(" %c ", row[j]);     // second column
                } else {
                    System.out.printf("%c |\n", row[j]);   // third column + right border
                }
            }
        }
        System.out.println("---------");                   // lower border
    }

    /**
     * Used for getting the right input from the user.
     */
    static void play() {
        System.out.print("Enter the coordinates: ");
        boolean valid = false;
        int one = 0;
        int two = 0;
        do {
            try {
                String line = SCANNER.nextLine();                                 // user input
                String[] points = line.split("\\s+");                       // expects 2 comma separated numbers
                one = Integer.parseInt(points[0]);                                // if more tokens, ignore them
                two = Integer.parseInt(points[1]);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                System.out.println("You should enter numbers!");                 // if parsing failed
            }
            if (one > 0 && one < 4 && two > 0 && two < 4) {                      // if both numbers (1-3)
                valid = true;
            } else {                                                             // if not
                System.out.println("Coordinates should be from 1 to 3!");
            }
        } while(!valid);
        put(--one, --two);                                                       // make a move
    }

    /**
     * Put a corresponding mark (O or X) on the field, if possible.
     * @param row the row value
     * @param col the column value
     */
    private static void put(int row, int col) {
        if (FIELD[row][col] == ' ') {                                           // if nothing there
            FIELD[row][col] = (move % 2 == 0) ? 'O' : 'X';                      // put O or X based on move
            printField();
            move++;
            checkState();
        } else {                                                                // if something there
            System.out.println("This cell is occupied! Choose another one!");
            play();                                                             // try again
        }
    }

    /**
     * Checks the state of the game after every valid move.
     */
    private static void checkState() {
        char winner = 0;
        int row = FIELD[0][0] + FIELD[1][1] + FIELD[2][2];     // sum first diagonal
        int col = FIELD[2][0] + FIELD[1][1] + FIELD[0][2];     // sum second diagonal

        if (row == 237 || col == 237) {                        // 3 Os give 237
            winner = 'O';
        } else if (row == 264 || col == 264) {                 // 3 Xs give 264
            winner = 'X';
        }

        for (int i = 0; i < FIELD.length; i++) {               // same for row & column
            row = col = 0;                                     // reset after each
            for (int j = 0; j < FIELD[i].length; j++) {
                row += FIELD[i][j];
                col += FIELD[j][i];
            }
            if (row == 237 || col == 237) {
                winner = 'O';
            } else if (row == 264 || col == 264) {
                winner = 'X';
            }
        }
        if (winner == 79) {                                    // 79 = O in Unicode
            System.out.println("O wins");
        } else if (winner == 88) {                             // 88 = X in Unicode
            System.out.println("X wins");
        } else if (move == 10) {                               // next move would be 10th, meaning the field is full
            System.out.println("Draw");
        } else {
            play();                                            // if no winner yet
        }
    }
}
