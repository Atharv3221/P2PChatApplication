package main.java.com.p2pchatapplication;

import java.io.*;
import java.util.*;

public class UserManager {
    private static final String CREDENTIALS_FILE = "src/main/resources/my_credentials.txt";
    private static final String USERS_FILE = "src/main/resources/users.txt";
    private static final List<String> RESERVED_COMMANDS = Arrays.asList("@Users", "@Add", "@Exit");

    public static String[] getUserCredentials() {
        File file = new File(CREDENTIALS_FILE);
        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String username = reader.readLine();
            String userId = reader.readLine();
            return new String[]{username, userId};
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveUserCredentials(String username, String userId) {
        if (isInvalidUsername(username)) {
            System.out.println("Invalid username! Please avoid using commands like @Users, @Add, @Exit.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CREDENTIALS_FILE))) {
            writer.write(username);
            writer.newLine();
            writer.write(userId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean addUserToFile(String username, String userId) {
        if (isInvalidUsername(username)) return false;

        List<String> users = getUsersFromFile();
        for (String user : users) {
            if (user.contains(userId)) return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            writer.write(username + " " + userId);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static String generateRandomId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static List<String> getUsersFromFile() {
        List<String> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                users.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    private static boolean isInvalidUsername(String username) {
        return RESERVED_COMMANDS.contains(username) || username.startsWith("@");
    }
}
