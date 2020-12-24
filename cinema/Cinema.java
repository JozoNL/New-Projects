package cinema;

import java.util.Scanner;

/**
 * Class for managing cinema seating arrangement, tickets and income.
 */
public class Cinema {
    private static final Scanner SCANNER = new Scanner(System.in);    // for user input
    private static char[][] seats;                                    // seating arrangement
    private static int income;                                        // money earned selling tickets
    private static int sold;                                          // tickets sold
    private static int row;                                           // number of rows
    private static int col;                                           // number of seats in each row

    public static void main(String[] args) {
        System.out.println("Enter the number of rows:");
        row = validate(2, 9);
        System.out.println("Enter the number of seats in each row:");
        col = validate(2, 9);
        createArrangement(row + 1, col + 1);
        showMenu();
    }

    /**
     * Creates seating arrangements according to the users input.
     * @param row the number of rows
     * @param col the number of seats in each row
     */
    private static void createArrangement(int row, int col) {
        seats = new char[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (i == 0 && j == 0) {
                    seats[i][j] = ' ';                              // make upper left corner empty
                } else if (j == 0) {
                    seats[i][j] = Character.forDigit(i, 10);  // put numbers on the first row (2-9) max
                } else if (i == 0) {
                    seats[i][j] = Character.forDigit(j, 10);  // put numbers on the first column (2-9) max
                } else {
                    seats[i][j] = 'S';                             // mark seat with S
                }
            }
        }
    }

    /**
     * Asks the user for the input until he inputs legal values.
     * @param from the lower bound
     * @param to the upper bound
     * @return the number that was input
     */
    private static int validate(int from, int to) {
        boolean valid = false;
        int input = -1;
        while (!valid) {
            try {
                input = Integer.parseInt(SCANNER.nextLine());
                if (input >= from && input <= to)
                    valid = true;
                else
                    System.out.printf("Wrong input. Allowed range is from %d to %d.\n", from, to);
            } catch (NumberFormatException e) {
                System.out.printf("Wrong input. Input numbers from %d to %d.\n", from, to);
            }
        }
        return input;
    }

    /**
     * Shows the menu and proceeds according to the users selection.
     */
    private static void showMenu() {
        int option = -1;
        while (option != 0) {
            System.out.println("\n1 Show the seats");
            System.out.println("2 Buy a ticket");
            System.out.println("3 Statistics");
            System.out.println("0 Exit");
            option = validate(0, 3);
            switch (option) {
                case 1 -> showSeats();
                case 2 -> buyTicket();
                case 3 -> showStats();
                case 0 -> System.out.println("\nBye!");
            }
        }
    }

    /**
     * Shows the seating arrangement.
     */
    private static void showSeats() {
        System.out.println("\nCinema:");
        for (char[] row : seats) {
            for (char seat : row)
                System.out.print(seat + " ");
            System.out.println();
        }
    }

    /**
     * Buys the ticket for a specific seat, if it was not sold already.
     */
    private static void buyTicket() {
        System.out.println("\nEnter a row number:");
        int rowNum = validate(1, row);
        System.out.println("Enter a seat number in that row:");
        int colNum = validate(1, col);
        if (seats[rowNum][colNum] == 'B') {
            System.out.println("\nThat ticket has already been purchased!");
            return;
        } else if (rowNum <= row / 2 || row * col < 60) {    // in < 60 seats cinemas all seats are $10
            System.out.println("\nTicket price: $10");       // first half is $10 in bigger cinemas
            income += 10;
        } else {
            System.out.println("\nTicket price: $8");        // second half is $8 in bigger cinemas
            income += 8;
        }
        sold++;
        seats[rowNum][colNum] = 'B';                         // if ticket was sold for a seat it is marked with (B)
    }

    /**
     * Shows number of tickets sold, current income and possible income provided all tickets will be sold.
     */
    private static void showStats() {
        int seats = row * col;
        System.out.printf("\nNumber of purchased tickets: %d\n", sold);
        System.out.printf("Percentage: %.2f%%\n", (double) sold * 100 / seats);
        System.out.printf("Current income: $%d\n", income);
        System.out.print("Possible income: ");
        if (seats < 60) {
            System.out.printf("$%d\n", seats * 10);
        } else {
            int total = 0;
            int half = row / 2;
            total += half * col * 10;
            total += (row - half) * (8 * col);
            System.out.printf("$%d\n", total);
        }
    }
}
