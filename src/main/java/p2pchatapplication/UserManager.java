package p2pchatapplication;

import java.io.*;
import java.util.Scanner;

/**
 * Manages the local user's credentials by reading/writing to my_credentials.txt.
 */
public class UserManager {
    private static final String CREDENTIAL_FILE = System.getProperty("user.dir") + File.separator +
            "src" + File.separator + "main" + File.separator + "resources" + File.separator + "my_credentials.txt";

    private static String myUsername;

    /**
     * Initializes user credentials by reading from the file.
     * If the file is empty, prompts the user to enter a new username and saves it.
     */
    public void initializeCredential() {
        File myCredential = new File(CREDENTIAL_FILE);
        try {
            if (myCredential.length() > 0) {
                // File has content
                BufferedReader reader = new BufferedReader(new FileReader(myCredential));
                myUsername = reader.readLine().trim();
                reader.close();
                System.out.println("Welcome back, " + myUsername + "!");
            } else {
                // File is empty
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter a new username: ");
                myUsername = scanner.nextLine().trim();

                BufferedWriter writer = new BufferedWriter(new FileWriter(myCredential));
                writer.write(myUsername);
                writer.close();
                System.out.println("Username saved as: " + myUsername);
            }
        } catch (IOException e) {
            System.out.println("[UserManager] Error handling credentials: " + e.getMessage());
        }
    }

    /**
     * Returns the currently loaded username.
     *
     * @return The user's username
     */
    public static String getMyUsername() {
        return myUsername;
    }
}
