package budget;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Class for creating and running the budget manager.
 */
public class Manager {
    public static void main(String[] args) {
        BudgetManager manager = new BudgetManager();
        manager.manage();
    }
}

/**
 * Class for working with the budget manager.
 */
class BudgetManager {
    private static final Scanner SCANNER = new Scanner(System.in);          // for user input
    private static final StringBuilder MAIN_MENU;                           // text for main menu
    private static final StringBuilder ADD_PURCHASE_MENU;                   // text for add purchase menu
    private static final StringBuilder SHOW_PURCHASES_MENU;                 // text for show purchases menu
    private static final StringBuilder SORT_MENU;                           // text for sort menu
    private static final StringBuilder SORT_SUBMENU;                        // text for sort submenu
    private final ArrayList<Category> categories = new ArrayList<>() {{     // categories for spending
        add(new Category("Food"));
        add(new Category("Clothes"));
        add(new Category("Entertainment"));
        add(new Category("Other"));
        add(new Category("All"));
    }};
    private double budget;                                                  // current budget

    /*
     * Creates the text fot the menus.
     */
    static {
        MAIN_MENU = new StringBuilder();
        MAIN_MENU.append("\nChoose your action:");
        MAIN_MENU.append("\n1) Add income");
        MAIN_MENU.append("\n2) Add purchase");
        MAIN_MENU.append("\n3) Show list of purchases");
        MAIN_MENU.append("\n4) Balance");
        MAIN_MENU.append("\n5) Save");
        MAIN_MENU.append("\n6) Load");
        MAIN_MENU.append("\n7) Analyze (Sort)");
        MAIN_MENU.append("\n0) Exit");
        ADD_PURCHASE_MENU = new StringBuilder();
        ADD_PURCHASE_MENU.append("\nChoose the type of purchase");
        ADD_PURCHASE_MENU.append("\n1) Food");
        ADD_PURCHASE_MENU.append("\n2) Clothes");
        ADD_PURCHASE_MENU.append("\n3) Entertainment");
        ADD_PURCHASE_MENU.append("\n4) Other");
        ADD_PURCHASE_MENU.append("\n5) Back");
        SHOW_PURCHASES_MENU = new StringBuilder();
        SHOW_PURCHASES_MENU.append("\nChoose the type of purchases");
        SHOW_PURCHASES_MENU.append("\n1) Food");
        SHOW_PURCHASES_MENU.append("\n2) Clothes");
        SHOW_PURCHASES_MENU.append("\n3) Entertainment");
        SHOW_PURCHASES_MENU.append("\n4) Other");
        SHOW_PURCHASES_MENU.append("\n5) All");
        SHOW_PURCHASES_MENU.append("\n6) Back");
        SORT_MENU = new StringBuilder();
        SORT_MENU.append("\nHow do you want to sort?");
        SORT_MENU.append("\n1) Sort all purchases");
        SORT_MENU.append("\n2) Sort by type");
        SORT_MENU.append("\n3) Sort certain type");
        SORT_MENU.append("\n4) Back");
        SORT_SUBMENU = new StringBuilder(ADD_PURCHASE_MENU.subSequence(0, 73));
    }

    /**
     * Manages finances until user exits the application.
     */
    void manage() {
        int option = -1;
        while (option != 0) {                                   // break if 0
            option = checkInput(MAIN_MENU, 0, 7);     // valid values (0-7)
            process(option);                                    // proceed
        }
        System.out.println("\nBye!");
    }

    /**
     * Asks the user to input valid value, until he does so.
     * @param menu menu to display
     * @param start the lower bound for legal input
     * @param end the upper bound for legal input
     * @return the number that was input
     */
    private int checkInput(StringBuilder menu, int start, int end) {
        int input = 0;
        boolean valid = false;
        do {
            System.out.println(menu);                                   // display the menu
            try {
                input = Integer.parseInt(SCANNER.nextLine());           // if input can be parsed to int
                valid = true;                                           // parsed successfully
            } catch (NumberFormatException e) {
                System.out.println("\nWrong input, try again!");        // else invalid
            }
            if (input < start || input > end) {                         // if successfully parsed but not within bounds
                System.out.println("\nWrong input, try again!");
                valid = false;                                          // still invalid
            }
        } while (!valid);
        return input;
    }

    /**
     * Used to process user's selected option.
     * @param option the number input by the user
     */
    private void process(int option) {
        switch (option) {
            case 1 -> addIncome();
            case 2 -> addPurchase();
            case 3 -> showPurchases();
            case 4 -> balance();
            case 5 -> save();
            case 6 -> load();
            case 7 -> sort();
        }
    }

    /**
     * Asks the user to input valid floating point number, until he does so.
     * @return the number that was input
     */
    private double validate() {
        double input = 0;
        boolean valid = false;
        do {
            try {
                input = Double.parseDouble(SCANNER.nextLine());
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Enter a number!");
            }
        } while (!valid);
        return input;
    }

    /**
     * Adds the specific amount of money to the budget.
     */
    private void addIncome() {
        System.out.println("\nEnter income:");
        double income = validate();
        budget += income;
        System.out.println("Income was added!");
    }

    /**
     * Adds a specific item to the list of the items according to its category and decrements price of the
     * item from the budget.
     */
    private void addPurchase() {
        int option = checkInput(ADD_PURCHASE_MENU, 1, 5);     // valid values (1-5)
        if (option == 5) {                                              // if 5
            return;                                                     // return to main menu
        }
        System.out.println("\nEnter purchase name:");
        String purchase = SCANNER.nextLine();                           // name of the product
        System.out.println("Enter its price:");
        double price = validate();                                      // price of the product
        String item = String.format("%s $%.2f", purchase, price);
        categories.get(categories.size() - 1).getItems().add(item);     // add item to list All
        categories.get(categories.size() - 1).addToSum(price);          // add money spent to list All
        categories.get(option - 1).getItems().add(item);                // add item to specific list
        categories.get(option - 1).addToSum(price);                     // add money spent to specific list
        System.out.println("Purchase was added!");
        budget -= price;                                                // decrement price from the budget
        addPurchase();                                                  // stay in this menu
    }

    /**
     * Shows the purchases for the selected list.
     */
    private void showPurchases() {
        if (categories.get(categories.size() - 1).getItems().isEmpty()) {   // if not a single item
            System.out.println("\nPurchase list is empty!");
            return;                                                         // return to main menu
        }
        int option = checkInput(SHOW_PURCHASES_MENU, 1, 6);       // valid values (1-6)
        if (option == 6) {                                                  // if 6
            return;                                                         // return no main menu
        }
        Category category = categories.get(--option);                       // get the selected category
        System.out.printf("\n%s:\n", category.getName());
        if (category.getItems().isEmpty()) {                                // if no items in this category
            System.out.println("Purchase list is empty!");
        } else {
            category.getItems().forEach(System.out::println);               // if something there
            System.out.printf("Total sum: $%.2f\n", category.getSum());     // total sum of the lists products
        }
        showPurchases();                                                    // stay in this menu
    }

    /**
     * Shows the current balance.
     */
    private void balance() {
        System.out.printf("\nBalance: $%.2f\n", budget);
    }

    /**
     * Saves the current balance and purchases to the file purchases.txt.
     */
    private void save() {
        try (PrintWriter writer = new PrintWriter("purchases.txt")) {
            writer.println(budget);                                    // save the budget
            for (Category category : categories) {
                ArrayList<String> list = category.getItems();
                for (String item : list) {
                    writer.println(item);                              // save the products
                }
                writer.println("End!");                                // end of category
                writer.println(category.getSum());                     // save total sum for a category
            }
        } catch (FileNotFoundException e) {
            System.out.println("File was not found!");
        }
        System.out.println("\nPurchases were saved!");
    }

    /**
     * Loads the saved balance and purchases from the file purchases.txt.
     */
    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader("purchases.txt"))) {
            budget += Double.parseDouble(reader.readLine());              // add loaded budget
            for (Category category : categories) {
                String item;
                while (true) {
                    item = reader.readLine();                             // load the products
                    if (item.equals("End!")) {                            // end of category
                        break;
                    }
                    category.getItems().add(item);                        // add to the list
                }
                category.addToSum(Double.parseDouble(reader.readLine())); // change the total sum for the category
            }
        } catch (FileNotFoundException e) {
            System.out.println("File was not found!");
        } catch (IOException e) {
            System.out.println("Some I/O error has occurred!");
        }
        System.out.println("\nPurchases were loaded!");
    }

    /**
     * Shows the sorted views of the lists based on user input.
     */
    private void sort() {
        int option = checkInput(SORT_MENU, 1, 4);   // valid values (1-4)
        switch (option) {
            case 1:
                sortCategory(categories.size() - 1);
                break;
            case 2:
                sortByType();
                break;
            case 3:
                sortCertainType();
                break;
            case 4:
                return;                                      // return to main menu
        }
        sort();                                              // stay in this menu
    }

    /**
     * Shows the view of a specific category of purchases sorted descending.
     * @param index index of the list to sort
     */
    private void sortCategory(int index) {
        ArrayList<String> list = categories.get(index).getItems();
        if (list.isEmpty()) {                                                 // if list empty
            System.out.println("\nPurchase list is empty!");
            return;                                                           // do nothing
        }

        String[] arr = new String[list.size()];
        list.toArray(arr);
        String[] sorted = sortArray(arr);
        System.out.printf("\n%s:\n", categories.get(index).getName());        // name of the category
        Stream.of(sorted).forEach(System.out::println);                       // items
        System.out.printf("Total: $%.2f\n", categories.get(index).getSum());  // total sum
    }

    /**
     * Shows descending list of money spent based on categories.
     */
    private void sortByType() {
        String[] arr = new String[categories.size() - 1];                                 // exclude category All
        for (int i = 0; i < categories.size() - 1; i++) {
            double sum = categories.get(i).getSum();                                      // sum for a category
            arr[i] = (sum == 0) ? String.format("%s: $0", categories.get(i).getName())
                    : String.format("%s: $%.2f", categories.get(i).getName(), sum);
        }
        String[] sorted = sortArray(arr);
        System.out.println("\nTypes:");
        Stream.of(sorted).forEach(System.out::println);                                   // sorted categories
        double sum = categories.get(categories.size() - 1).getSum();                      // sum of all
        System.out.printf((sum == 0) ? "Total sum: $0\n" : "Total sum: $%.2f\n", sum);
    }

    /**
     * Sorts the array of prices descending.
     * @param arr array to be sorted
     * @return sorted array
     */
    private String[] sortArray(String[] arr) {
        int iterations = arr.length - 1;
        while (iterations > 0) {
            for (int i = 0; i < iterations; i++) {
                String first = arr[i];
                String second = arr[i + 1];
                String[] priceOfFirst = first.split("\\$");
                double firstPrice = Double.parseDouble(priceOfFirst[priceOfFirst.length - 1]);
                String[] priceOfSecond = second.split("\\$");
                double secondPrice = Double.parseDouble(priceOfSecond[priceOfSecond.length - 1]);
                if (firstPrice < secondPrice) {
                    arr[i] = arr[i + 1];
                    arr[i + 1] = first;
                }
            }
            iterations--;
        }
        return arr;
    }

    /**
     * Sorts the selected category of items descending based on price.
     */
    private void sortCertainType() {
        int option = checkInput(SORT_SUBMENU, 1, 4);                // valid values (1-4)
        sortCategory(--option);
    }
}

/**
 * Class to create a specific category of purchases.
 */
class Category {
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

    /**
     * Setter for a sum of this category.
     * @param sum the new sum to be set
     */
    public void setSum(double sum) {
        this.sum = sum;
    }
}
