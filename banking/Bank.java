package banking;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Class for managing the accounts and operations with them.
 */
public class Bank {
    private static final StringBuilder LOGGED_MENU = new StringBuilder(); // menu shown after logging
    private static final StringBuilder MAIN_MENU = new StringBuilder();   // main menu
    private static final Scanner SCANNER = new Scanner(System.in);        // for user input
    private static String db;                                             // database string

    public static void main(String[] args) {
        db = (args.length == 2 && args[0].equals("-fileName")) ? args[1]
                                                               : "test.db";
        createTable();
        showMenu();
    }

    /*
     * creates the text for the menus
     */
    static {
        MAIN_MENU.append("\n1 Create an account");
        MAIN_MENU.append("\n2 Log into account");
        MAIN_MENU.append("\n0 Exit");
        LOGGED_MENU.append("\n1 Balance");
        LOGGED_MENU.append("\n2 Add money");
        LOGGED_MENU.append("\n3 Transfer money");
        LOGGED_MENU.append("\n4 Change PIN");
        LOGGED_MENU.append("\n5 Close account");
        LOGGED_MENU.append("\n6 Log out");
        LOGGED_MENU.append("\n0 Exit");
    }

    /**
     * Keeps the application going until 0 is input.
     */
    private static void showMenu() {
        int option = -1;
        while (option != 0) {
            System.out.println(MAIN_MENU);
            option = validateInput(0, 2);
            if (option == 1)
                createAccount();
            else if (option == 2)
                logIn();
        }
        System.out.println("\nBye.");
    }

    /**
     * Validates that the input is within the bounds.
     * @param from the lower bound
     * @param to the upper bound
     * @return the number that was input
     */
    private static int validateInput(int from, int to) {
        int input = -1;
        boolean valid = false;
        while (!valid) {
            try {
                input = Integer.parseInt(SCANNER.nextLine());
                valid = true;
            } catch (NumberFormatException e) {
                System.out.println("Enter whole numbers.");
            }
            if (input < from || input > to) {
                System.out.printf("Valid range is from %d to %d.\n", from, to);
                valid = false;
            }
        }
        return input;
    }

    /**
     * Creates an account with random account number and PIN. Fails in case the account number already exists.
     */
    private static void createAccount() {
        StringBuilder number = rng(new StringBuilder("400000"), 9);
        StringBuilder pin = rng(new StringBuilder(), 4);
        number.append(calculateChecksum(number));
        if (isInDatabase(number.toString())) {
            System.out.println("Something's wrong, please try again.");
            return;
        }
        System.out.println("\nYour card has been created.");
        System.out.printf("Your card number:\n%s\n", number);
        System.out.printf("Your card PIN:\n%s\n", pin);
        insert(number.toString(), pin.toString());
    }

    /**
     * Appends a specific number of random digits.
     * @param builder object of StringBuilder to append to
     * @param length number of digits to append
     * @return StringBuilder object with the digits appended
     */
    private static StringBuilder rng(StringBuilder builder, int length) {
        for (int i = 0; i < length; i++)
            builder.append((int) (Math.random() * 10));
        return builder;
    }

    /**
     * Connects to the database and returns the Connection object.
     * @return the Connection object
     */
    private static Connection connect() {
        String url = "jdbc:sqlite:" + db;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Failed to connect to a database.");
        }
        return connection;
    }

    /**
     * Creates the accounts table, in case it does not yet exists.
     */
    private static void createTable() {
        String create = "CREATE TABLE IF NOT EXISTS accounts (\n" +
                "        id INTEGER PRIMARY KEY,\n" +
                "        number TEXT,\n" +
                "        pin TEXT,\n" +
                "        balance INTEGER DEFAULT 0\n" +
                ");";
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute(create);
        } catch (SQLException e) {
            System.out.println("Failed to create a table.");
        }
    }

    /**
     * Adds the new account to the table with the specific number and pin.
     * @param number the account number
     * @param pin the pin to log in with
     */
    private static void insert(String number, String pin) {
        String insert = "INSERT INTO accounts(number, pin) VALUES(?,?)";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(insert)) {
            statement.setString(1, number);
            statement.setString(2, pin);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to insert data.");
        }
    }

    /**
     * Calculates the last digit for the account number based on the checksum algorithm.
     * @param number the account number to calculate the checksum for
     * @return the checksum digit
     */
    private static int calculateChecksum(StringBuilder number) {
        int sum = 0;
        for (int i = 0; i < number.length(); i++) {
            int digit = number.charAt(i) - 48;
            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9)
                    digit -= 9;
            }
            sum += digit;
        }
        return (sum % 10 == 0) ? 0 : 10 - (sum % 10);
    }

    /**
     * Used to log in into the specific account.
     */
    private static void logIn() {
        System.out.println("\nEnter your card number:");
        String number = SCANNER.nextLine();
        System.out.println("Enter your PIN:");
        String pin = SCANNER.nextLine();
        String account = validateLogin(number, pin);
        if (account.isEmpty())
            System.out.println("\nWrong card number or PIN.");
        else
            loggedIn(account);
    }

    /**
     * Gets the account number for the number and pin input if it exists.
     * @param number the account number
     * @param pin the pin of the account
     * @return the account number if input values were correct, otherwise an empty String.
     */
    private static String validateLogin(String number, String pin) {
        String select = "SELECT number FROM accounts WHERE number = ? AND pin = ?";
        String check = "";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setString(1, number);
            statement.setString(2, pin);
            ResultSet set = statement.executeQuery();
            if (set.next())
                check = set.getString("number");
        } catch (SQLException e) {
            System.out.println("Failed to validate login.");
        }
        return check;
    }

    /**
     * Menu after logging into the account.
     * @param account the account logged in
     */
    private static void loggedIn(String account) {
        System.out.println("\nYou have successfully logged in.");
        loop: while (true) {
            System.out.println(LOGGED_MENU);
            int option = validateInput(0, 6);
            switch (option) {
                case 1:
                    System.out.printf("\nBalance: %d\n", balance(account));
                    break;
                case 2:
                    System.out.println("\nEnter amount:");
                    int amount = validateInput(1, Integer.MAX_VALUE);
                    addMoney(account, amount);
                    System.out.println("Money were added.");
                    break;
                case 3:
                    validateTransfer(account, balance(account));
                    break;
                case 4:
                    validateNewPin(account);
                    break;
                case 5:
                    closeAccount(account);
                    break loop;
                case 6:
                    System.out.println("\nYou have successfully logged out.");
                    break loop;
                case 0:
                    System.out.println("\nBye.");
                    System.exit(0);
            }
        }
    }

    /**
     * Gets the balance for the specific account.
     * @param number the account number to get the balance for
     * @return the balance
     */
    private static int balance(String number) {
        String select = "SELECT balance FROM accounts WHERE number = ?";
        int balance = -1;
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setString(1, number);
            ResultSet set = statement.executeQuery();
            if (set.next())
                balance = set.getInt("balance");
        } catch (SQLException e) {
            System.out.println("Failed querying balance.");
        }
        return balance;
    }

    /**
     * Adds the specific amount of money to the account.
     * @param number the account to add the money for
     * @param amount the amount of money to be added
     */
    private static void addMoney(String number, int amount) {
        String update = "UPDATE accounts SET balance = balance + ? WHERE number = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(update)) {
            statement.setInt(1, amount);
            statement.setString(2, number);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed adding money.");
        }
    }

    /**
     * Checks if the money can be transferred to the specific account.
     * @param numberFrom the account from which the money will be transferred
     * @param balance the balance on that account
     */
    private static void validateTransfer(String numberFrom, int balance) {
        System.out.println("\nTransfer");
        System.out.println("Enter card number:");
        String numberTo = SCANNER.nextLine();
        if (numberTo.length() != 16) {
            System.out.println("Only 16 digit numbers allowed.");
            return;
        }
        StringBuilder check = new StringBuilder(numberTo).deleteCharAt(numberTo.length() - 1);
        int checksum = calculateChecksum(check);
        if (numberFrom.equals(numberTo)) {
            System.out.println("You can't transfer money to the same account.");
        } else if (checksum != numberTo.charAt(15) - 48) {
            System.out.println("You have probably made a mistake in the card number. Please try again.");
        } else if (!isInDatabase(numberTo)) {
            System.out.println("Such a card does not exist.");
        } else {
            System.out.println("Enter how much money you want to transfer:");
            int amount = validateInput(1, Integer.MAX_VALUE);
            if (amount > balance)
                System.out.println("Not enough money.");
            else
                transferMoney(numberFrom, numberTo, amount);
        }
    }

    /**
     * Transfers the money from one account to another.
     * @param numberFrom the account from which the money will be transferred
     * @param numberTo the account the money will transfer to
     * @param amount the amount of money to transfer
     */
    private static void transferMoney(String numberFrom, String numberTo, int amount) {
        String update = "UPDATE accounts SET balance = balance - ? WHERE number = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(update)) {
            statement.setInt(1, amount);
            statement.setString(2, numberFrom);
            statement.executeUpdate();
            System.out.println("Money successfully transferred.");
        } catch (SQLException e) {
            System.out.println("Failed transferring money.");
        }
        addMoney(numberTo, amount);
    }

    /**
     * Checks if while changing the PIN, the user's input is valid.
     * @param number the account number to change the PIN for
     */
    private static void validateNewPin(String number) {
        System.out.println("\nEnter new 4 digit PIN:");
        String newPin1 = SCANNER.nextLine();
        if (newPin1.length() != 4) {
            System.out.println("Wrong length.");
            return;
        }
        char[] parts = newPin1.toCharArray();
        for (char c : parts) {
            if (c < 48 || c > 57) {
                System.out.println("Only digits 0-9 allowed.");
                return;
            }
        }
        System.out.println("Enter new PIN again:");
        String newPin2 = SCANNER.nextLine();
        if (newPin1.equals(newPin2))
            changePin(number, newPin1);
        else
            System.out.println("PINs don't match.");
    }

    /**
     * Changes the PIN for the specific account.
     * @param number the account to change the PIN for
     * @param newPin the new PIN to be set
     */
    private static void changePin(String number, String newPin) {
        String update = "UPDATE accounts SET pin = ? WHERE number = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(update)) {
            statement.setString(1, newPin);
            statement.setString(2, number);
            statement.executeUpdate();
            System.out.println("PIN has been changed.");
        } catch (SQLException e) {
            System.out.println("Failed to change PIN.");
        }
    }

    /**
     * Closes the account.
     * @param number the account number to close
     */
    private static void closeAccount(String number) {
        String delete = "DELETE FROM accounts WHERE number = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(delete)) {
            statement.setString(1, number);
            statement.executeUpdate();
            System.out.println("\nThe account has been closed.");
        } catch (SQLException e) {
            System.out.println("Failed to close account.");
        }
    }

    /**
     * Checks if the specific account number is in the database.
     * @param number the account number to search for
     * @return {@code true} if the number exists, otherwise {@code false}
     */
    private static boolean isInDatabase(String number) {
        String select = "SELECT number FROM accounts WHERE number = ?";
        try (Connection connection = connect();
             PreparedStatement statement = connection.prepareStatement(select)) {
            statement.setString(1, number);
            ResultSet set = statement.executeQuery();
            String account = "";
            if (set.next())
                account = set.getString("number");
            return account.equals(number);
        } catch (SQLException e) {
            System.out.println("Failed querying database.");
            return false;
        }
    }
}
