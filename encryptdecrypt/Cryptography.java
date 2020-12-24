package encryptdecrypt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Class used for encryption and decryption.
 */
public class Cryptography {
    private static String in = "";                              // if input from file (default: no)
    private static String out = "";                             // if output to file (default: no)
    private static StringBuilder data = new StringBuilder();    // data to process
    private static String alg = "shift";                        // algorithm used (default: shift)
    private static String mode = "enc";                         // mode (enc for encrypting dec for decrypting)
    private static int key = 0;                                 // key to be used (default: 0 does nothing)

    /**
     * Program works with command line arguments, output will vary based on different flags used.
     * @param args array of arguments that is expected to be up to 10 length
     */
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "-in" -> in = args[i + 1];
                case "-out" -> out = args[i + 1];
                case "-data" -> data = new StringBuilder(args[i + 1]);
                case "-alg" -> alg = args[i + 1];
                case "-mode" -> mode = args[i + 1];
                case "-key" -> key = Integer.parseInt(args[i + 1]);
            }
        }
        if (!in.isEmpty())                        // if input is from a file
            input();
        encryptDecrypt((mode.equals("enc")));
    }

    /**
     * Processes the data based on the chosen options.
     * @param encrypt {@code true} if encryption is chosen, otherwise {@code false}
     */
    private static void encryptDecrypt(boolean encrypt) {
        char[] parts = data.toString().toCharArray();
        if (alg.equals("shift"))
            shift(parts, encrypt);
        else
            unicode(parts, encrypt);
    }

    /**
     * One of the algorithms used for encryption/decryption. Uses the key for alphabetical values,
     * does not change the rest. Only valid keys are (1-25) as (0 and 26) do nothing.
     * @param parts array of characters to be processed
     * @param encrypt {@code true} if encryption is chosen, otherwise {@code false}
     */
    private static void shift(char[] parts, boolean encrypt) {
        for (int i = 0; i < parts.length; i++) {
            if (encrypt) {                                         // encrypt
                if (parts[i] >= 65 && parts[i] <= 90) {            // alphabet upper case
                    parts[i] += key;                               // increment by key
                    if (parts[i] > 90)                             // if over the upper bound
                        parts[i] -= 26;                            // go around
                } else if (parts[i] >= 97 && parts[i] <= 122) {    // alphabet lower case
                    parts[i] += key;                               // increment by key
                    if (parts[i] > 122)                            // if over the upper bound
                        parts[i] -= 26;                            // go around
                }
            } else {                                               // decrypt
                if (parts[i] >= 65 && parts[i] <= 90) {            // alphabet upper case
                    parts[i] -= key;                               // decrement by key
                    if (parts[i] < 65)                             // if under the lower bound
                        parts[i] += 26;                            // go around
                } else if (parts[i] >= 97 && parts[i] <= 122) {    // alphabet lower case
                    parts[i] -= key;                               // decrement by key
                    if (parts[i] < 97)                             // if under the lower bound
                        parts[i] += 26;                            // go around
                }
            }
        }
        output(new String(parts));                                 // result
    }

    /**
     * One of the algorithms used for encryption/decryption. Shifts all unicode values except line feed (10).
     * @param parts array of characters to be processed
     * @param encrypt {@code true} if encryption is chosen, otherwise {@code false}
     */
    private static void unicode(char[] parts, boolean encrypt) {
        for (int i = 0; i < parts.length; i++) {
            if (encrypt && parts[i] != 10)     // encrypt
                parts[i] += key;
            else if (parts[i] != 10)           // decrypt
                parts[i] -= key;
        }
        output(new String(parts));             // result
    }

    /**
     * Reads the file in case file is used as the source of data.
     */
    private static void input() {
        try (BufferedReader reader = new BufferedReader(new FileReader(in))) {
            while (reader.ready()) {
                data.append(reader.readLine());  // append line read
                data.append((char) 10);          // + line feed
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        } catch (IOException e) {
            System.out.println("Some I/O error has occurred.");
        }
    }

    /**
     * Outputs the result of encryption/decryption either to the console or to the file.
     * @param result the result of encryption/decryption
     */
    private static void output(String result) {
        if (out.isEmpty()) {            // console
            System.out.println(result);
        } else {                        // file
            try (PrintWriter writer = new PrintWriter(out)) {
                writer.println(result);
            } catch (FileNotFoundException e) {
                System.out.println("File not found.");
            }
        }
    }
}
