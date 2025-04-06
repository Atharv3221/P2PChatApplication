package p2pchatapplication;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserManager {
    private static final String CREDENTIAL_FILE = "src/main/resources/my_credentials.txt";
    private static final String FRIENDS_FILE = "src/main/resources/users.json";

    public static String initializeUser() {
        File file = new File(CREDENTIAL_FILE);

        // Check if username already exists
        if (file.exists()) {
            try {
                String username = Files.readString(file.toPath()).trim();
                if (!username.isEmpty()) {
                    System.out.println("👋 Hello @" + username + ", welcome back! Application started.");
                    return username;
                }
            } catch (IOException e) {
                System.out.println("❌ Error reading username.");
            }
        }

        // Ask for username if not present
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine().trim();
        try {
            Files.writeString(file.toPath(), username, StandardCharsets.UTF_8);
            System.out.println("✅ Username saved. Welcome @" + username + "!");
        } catch (IOException e) {
            System.out.println("❌ Couldn't save username.");
        }
        return username;
    }

    public static Set<String> getAllFriends() {
        try {
            ensureJsonFileExists(); // Ensure file is present and valid
            String json = Files.readString(Paths.get(FRIENDS_FILE));
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            return obj.keySet();
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    public static boolean isFriend(String username) {
        return getAllFriends().contains(username);
    }

    public static void addFriend(String username) {
        try {
            ensureJsonFileExists();
            JsonObject obj = readUsersJson();
            if (obj.has(username)) {
                System.out.println("👥 @" + username + " is already added.");
                return;
            }
            obj.addProperty(username, true);
            writeJsonToFile(obj);
            System.out.println("✅ Added @" + username + " to friend list.");
        } catch (IOException e) {
            System.out.println("⚠️ Could not add friend.");
        }
    }

    private static JsonObject readUsersJson() {
        try {
            String json = Files.readString(Paths.get(FRIENDS_FILE));
            return JsonParser.parseString(json).getAsJsonObject();
        } catch (IOException e) {
            return new JsonObject();
        }
    }

    private static void writeJsonToFile(JsonObject obj) throws IOException {
        Gson gson = new Gson();
        String prettyJson = gson.toJson(obj);
        Files.write(Paths.get(FRIENDS_FILE), prettyJson.getBytes(StandardCharsets.UTF_8));
    }

    private static void ensureJsonFileExists() {
        Path path = Paths.get(FRIENDS_FILE);
        if (!Files.exists(path)) {
            try {
                Files.write(path, "{}".getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                System.out.println("⚠️ Could not create users.json");
            }
        }
    }
}
