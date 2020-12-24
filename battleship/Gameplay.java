package battleship;

import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;

/**
 * Class used for playing the game.
 */
public class Gameplay {
    public static void main(String[] args) {
        Field[] players = {new Field("Player 1"), new Field("Player 2")};
        for (Field player : players) {
            player.initShips(player.getName());
            Field.makeSpace();
        }
        boolean valid;
        int turn = 1;

        loop: while (true) {
            for (Field player : players) {
                player.printFields();
                System.out.printf("%s, it's your turn:\n", player.getName());
                do {
                    valid = (turn % 2 == 1) ? players[0].shoot(players[1].getOur())
                                            : players[1].shoot(players[0].getOur());
                } while (!valid);
                if (player.getAlive() == 0) {
                    break loop;
                }
                Field.makeSpace();
                turn++;
            }
        }
    }
}

/**
 * Class to create a field for the players.
 */
class Field {
    private static final ArrayList<ArrayList<String>> COORDINATES = new ArrayList<>();   // where the ships are
    private static final Scanner SCANNER = new Scanner(System.in);                       // for user input
    private final String[][] our = initField();                                          // this player field
    private final String[][] fog = initField();                                          // fog of war
    private final Ship[] ships = Ship.values();                                          // types of ships
    private final String name;                                                           // name of the player
    private int alive = 5;                                                               // ships alive, 5 at the start

    /**
     * Constructor for a Field. Takes only the name of the player.
     * @param name name of the player
     */
    public Field(String name) {
        this.name = name;
    }

    /**
     * Getter for the ships positioning.
     * @return 2D array with the ships positioning
     */
    public String[][] getOur() {
        return our;
    }

    /**
     * Getter for the player's name using this Field.
     * @return name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for a number of ships alive for the player using this Field.
     * @return number of ships alive
     */
    public int getAlive() {
        return alive;
    }

    /**
     * Used to initialize first row and column with numbers and letters and playing field 10x10 with water.
     * Tilde (~) is used to represent water.
     * @return the initial state of the playing field
     */
    private String[][] initField() {
        String[][] field = new String[11][11];
        char c = 65;                                      // A
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (i == 0 && j == 0) {
                    field[i][j] = " ";                    // make upper left corner empty
                } else if (i == 0) {
                    field[i][j] = String.valueOf(j);      // put numbers on the first row (1-10)
                } else if (j == 0) {
                    field[i][j] = String.valueOf(c++);    // put letters on the first column (A-J)
                } else {
                    field[i][j] = "~";                    // fill 10x10 playing field with water (~)
                }
            }
        }
        return field;
    }

    /**
     * Printing the current state of the field.
     * @param field the field to print
     */
    private void printField(String[][] field) {
        System.out.println();
        for (String[] row : field) {
            for (String point : row) {
                System.out.print(point + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Printing the current state of the fields. The first field is fog of war showing players shots and whether
     * or not they were successful. The second field shows players ships with enemy's shots.
     */
    void printFields() {
        printField(fog);                                // the first field (fog of war)
        System.out.println("---------------------");    // separator
        printField(our);                                // the second field (this players ships)
    }

    /**
     * Provides space in between moves.
     */
    static void makeSpace() {
        System.out.println("Press Enter and pass the move to another player");
        String input;
        do {
            input = SCANNER.nextLine();                 // user input
        } while (!input.isEmpty());                     // until it is not empty (press Enter)
        for (int i = 0; i < 50; i++) {
            System.out.println();                       // prints 50 empty lines
        }
    }

    /**
     * Initializes ships for the player.
     * @param player player to initialize ships for
     */
    void initShips(String player) {
        System.out.printf("%s, place your ships on the game field\n", player);
        printField(our);
        for (Ship ship : ships) {
            putShip(ship);
        }
    }

    /**
     * Put a specific ship on a field.
     * @param ship the ship to put on a field
     */
    private void putShip(Ship ship) {
        boolean valid;
        String[] points;
        System.out.printf("Enter the coordinates of the %s (%d cells):\n", ship.getType(), ship.getLength());
        do {
            do {
                String line = SCANNER.nextLine();           // user input
                points = line.split("\\s+");          // expects starting and ending coordinates for a ship
            } while (points.length != 2);                   // if two tokens
            valid = validate(ship, points[0], points[1]);   // check their validity
        } while (!valid);
        printField(our);                                    // print the field with the new ship
    }

    /**
     * Validates if the input coordinates for the ship are correct, if so adds them to the list.
     * @param ship ship to be validated
     * @param start starting coordinates for the ship
     * @param end ending coordinates for the ship
     * @return {@code true} if all tests pass, otherwise {@code false}
     */
    private boolean validate(Ship ship, String start, String end) {
        int firstCol;
        int secondCol;
        int firstRow;
        int secondRow;
        try {
            int first = Integer.parseInt(start.substring(1));                   // expects number (1-10)
            int second = Integer.parseInt(end.substring(1));                    // expects number (1-10)
            firstCol = Math.min(first, second);                                 // make firstCol smaller of the two
            secondCol = Math.max(first, second);                                // make secondCol bigger of the two
        } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
            return false;                                                       // if failed
        }
        int first = start.charAt(0);                                            // expects 65-74 (A-J)
        int second = end.charAt(0);                                             // expects 65-74 (A-J)
        firstRow = Math.min(first, second) - 64;                                // make firstRow smaller of the two
        secondRow = Math.max(first, second) - 64;                               // make secondRow bigger of the two

        if (!(checkCoordinates(firstRow, secondRow, firstCol, secondCol))) {
            System.out.println("Error! Wrong ship location! Try again:");
            return false;                                                       // if wrong coordinates
        }
        if (!(checkLength(firstRow, secondRow, firstCol, secondCol, ship.getLength()))) {
            System.out.printf("Error! Wrong length of the %s! Try again:\n", ship.getType());
            return false;                                                       // if ship has incorrect length
        }
        if (!(checkCollision(firstRow, secondRow, firstCol, secondCol, ship.getLength()))) {
            System.out.println("Error! You placed it too close to another one. Try again:");
            return false;                                                       // if ship is too close to another ship
        }

        ArrayList<String> list = new ArrayList<>();                             // list of coordinates the ship occupies
        for (int i = firstRow; i <= secondRow; i++) {
            for (int j = firstCol; j <= secondCol; j++) {
                list.add(i + " " + j);                                          // add comma separated coordinates
            }
        }
        COORDINATES.add(list);                                                  // add the list to the rest
        return true;
    }

    /**
     * Checks if the coordinates are valid.
     * @param row1 the first row
     * @param row2 the second row
     * @param col1 the first column
     * @param col2 the second column
     * @return {@code true} if the coordinates are valid, otherwise {@code false}
     */
    private boolean checkCoordinates(int row1, int row2, int col1, int col2) {
        int[] values = {row1, row2, col1, col2};

        for (int value : values) {
            if (!(value > 0 && value < 11)) {       // all 4 values are expected to be 1-10
                return false;
            }
        }
        return row1 == row2 ^ col1 == col2;         // either row or column has to be the same
    }

    /**
     * Checks if the length is correct for the corresponding ship.
     * @param row1 the first row
     * @param row2 the second row
     * @param col1 the first column
     * @param col2 the second column
     * @param length the expected length of the ship
     * @return {@code true} if the length is correct, otherwise {@code false}
     */
    private boolean checkLength(int row1, int row2, int col1, int col2, int length) {
        return (col1 == col2) ? row2 - row1 + 1 == length : col2 - col1 + 1 == length;
    }

    /**
     * Checks if this ship is not being put too close to another.
     * @param row1 the first row
     * @param row2 the second row
     * @param col1 the first column
     * @param col2 the second column
     * @param length the length of this ship
     * @return {@code true} if there was no collision, {@code false} otherwise
     */
    private boolean checkCollision(int row1, int row2, int col1, int col2, int length) {
        int checks = 0;                         // how many coordinates pass the check
        boolean first = true;                   // first coordinate to check
        if (row1 == row2) {                     // ship is horizontal
            int min = col1;                     // loop column
            for (; min <= col2; min++) {
                if (our[row1][min].equals("~") && horizontalCheck(row1, min, first)) {
                    checks++;                   // if successful
                }
                first = false;                  // all other coordinates are not first
            }
            if (length == checks) {             // if as many checks as ship's length
                for (; col1 <= col2; col1++) {
                    our[row1][col1] = "O";      // put the ship, represented by (O)
                }
                return true;
            }
        } else {                                // ship is vertical
            int min = row1;                     // loop row
            for (; min <= row2; min++) {
                if (our[min][col1].equals("~") && verticalCheck(min, col1, first)) {
                    checks++;                   // if successful
                }
                first = false;                  // all other coordinates are not first
            }
            if (length == checks) {             // if as many checks as ship's length
                for (; row1 <= row2; row1++) {
                    our[row1][col1] = "O";      // put the ship, represented by (O)
                }
                return true;
            }
        }
        return false;                           // if some coordinate collides with other ship
    }

    /**
     * Checks the vicinity of the specific coordinate to determine if it is safe to put a ship there.
     * @param i the row coordinate
     * @param j the column coordinate
     * @param first if the coordinate is first
     * @return {@code true} if the coordinate is appropriate, otherwise {@code false}
     */
    private boolean horizontalCheck(int i, int j, boolean first) {
        if (i == 10 && j == 10) {
            return our[i - 1][j].equals("~");
        } else if (j == 10) {
            return (!(our[i + 1][j].equals("O") || our[i - 1][j].equals("O")));
        } else if (i == 10 && first) {
            return (!(our[i - 1][j].equals("O") || our[i][j + 1].equals("O") || our[i][j - 1].equals("O")));
        } else if (i == 10) {
            return (!(our[i - 1][j].equals("O") || our[i][j + 1].equals("O")));
        } else if (first) {
            return (!(our[i + 1][j].equals("O") || our[i - 1][j].equals("O") || our[i][j + 1].equals("O") || our[i][j - 1].equals("O")));
        } else {
            return (!(our[i + 1][j].equals("O") || our[i - 1][j].equals("O") || our[i][j + 1].equals("O")));
        }
    }

    /**
     * Checks the vicinity of the specific coordinate to determine if is is safe to put a ship there.
     * @param i the row coordinate
     * @param j the column coordinate
     * @param first if the coordinate is first
     * @return {@code true} if the coordinate is appropriate, otherwise {@code false}
     */
    private boolean verticalCheck(int i, int j, boolean first) {
        if (i == 10 && j == 10) {
            return our[i][j - 1].equals("~");
        } else if (i == 10) {
            return (!(our[i][j + 1].equals("O") || our[i][j - 1].equals("O")));
        } else if (j == 10 && first) {
            return (!(our[i][j - 1].equals("O") || our[i + 1][j].equals("O") || our[i - 1][j].equals("O")));
        } else if (j == 10) {
            return (!(our[i][j - 1].equals("O") || our[i + 1][j].equals("O")));
        } else if (first) {
            return (!(our[i + 1][j].equals("O") || our[i - 1][j].equals("O") || our[i][j + 1].equals("O") || our[i][j - 1].equals("O")));
        } else {
            return (!(our[i + 1][j].equals("O") || our[i][j + 1].equals("O") || our[i][j - 1].equals("O")));
        }
    }

    /**
     * Checks the state of the game after each player's move.
     * @param field the field to shoot at
     * @return {@code true} if the player shot successfully, otherwise {@code false}
     */
    boolean shoot(String[][] field) {
        String shot = SCANNER.nextLine();                       // user input
        int row;
        int col;
        try {
            row = shot.charAt(0) - 64;                          // expects number (1-10)
            col = Integer.parseInt(shot.substring(1));          // expects number (1-10)
        } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
            System.out.println("Error! You entered the wrong coordinates! Try again:");
            return false;                                       // if failed
        }
        if (row < 1 || row > 10 || col < 1 || col > 10) {
            System.out.println("Error! You entered the wrong coordinates! Try again:");
            return false;                                       // if failed
        }

        switch (field[row][col]) {                              // if succeeded
            case "O" -> {                                       // if ship there
                field[row][col] = fog[row][col] = "X";          // hit it
                if (shipDown(row, col)) {                       // check if you sunk it
                    alive--;                                    // if yes, one less ship alive
                    System.out.println((alive == 0) ? "You sank the last ship. You won. Congratulations!"
                                                    : "You sank a ship!");
                } else {
                    System.out.println("You hit a ship!");
                }
            }
            case "~" -> {                                       // if water there
                field[row][col] = fog[row][col] = "M";          // mark coordinate with (M) as in miss
                System.out.println("You missed!");
            }
            default -> System.out.println("You fired at the same coordinates again!");
        }
        return true;
    }

    /**
     * Checks if the whole ship is destroyed.
     * @param i the row coordinate
     * @param j the column coordinate
     * @return {@code true} if the ship is destroyed, otherwise {@code false}
     */
    private boolean shipDown(int i, int j) {
        for (ArrayList<String> list : COORDINATES) {            // loop the ship's coordinates
            ListIterator<String> it = list.listIterator();
            while (it.hasNext()) {
                String point = it.next();
                String[] points = point.split("\\s+");
                int x = Integer.parseInt(points[0]);
                int y = Integer.parseInt(points[1]);
                if (i == x && j == y) {                         // find the matching coordinates
                    it.remove();                                // remove them
                }
            }
        }
        for (ArrayList<String> list : COORDINATES) {
            if (list.isEmpty()) {                               // if any of the lists is empty
                COORDINATES.remove(list);                       // remove it, the ship is gone
                return true;
            }
        }
        return false;                                           // the ship is still alive
    }
}

/**
 * Enum for the ship's constants.
 */
enum Ship {
    AIRCRAFT_CARRIER("Aircraft Carrier", 5), BATTLESHIP("Battleship", 4),
    SUBMARINE("Submarine", 3), CRUISER("Cruiser", 3), DESTROYER("Destroyer", 2);

    private final String type;              // type of the ship
    private final int length;               // length of the ship

    /**
     * Constructor for the ship.
     * @param type type of the ship
     * @param length length of the ship
     */
    Ship(String type, int length) {
        this.type = type;
        this.length = length;
    }

    /**
     * Getter for the length.
     * @return the length of the ship
     */
    public int getLength() {
        return length;
    }

    /**
     * Getter for the type.
     * @return the type of the ship
     */
    public String getType() {
        return type;
    }
}
