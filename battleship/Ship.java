package battleship;

/**
 * Enum for the ship's constants.
 */
public enum Ship {
    AIRCRAFT_CARRIER("Aircraft Carrier", 5),
    BATTLESHIP("Battleship", 4),
    SUBMARINE("Submarine", 3),
    CRUISER("Cruiser", 3),
    DESTROYER("Destroyer", 2);

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
