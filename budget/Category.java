package budget;

import java.util.ArrayList;

/**
 * Class to create a specific category of purchases.
 */
public class Category {
    private final ArrayList<String> items = new ArrayList<>();     // list of the items
    private final String name;                                     // name of the category
    private double sum;                                            // price of all the items summed

    /**
     * Constructor for a category.
     * @param name name of the category
     */
    public Category(String name) {
        this.name = name;
    }

    /**
     * Getter for the list of items of this category.
     * @return list of items of this category
     */
    public ArrayList<String> getItems() {
        return items;
    }

    /**
     * Getter for the name of this category.
     * @return name of this category
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for a sum of this category.
     * @return the sum of this category
     */
    public double getSum() {
        return sum;
    }

    /**
     * Adds the specific amount to the total sum.
     * @param toAdd the amount to be added
     */
    public void addToSum(double toAdd) {
        sum += toAdd;
    }
}
