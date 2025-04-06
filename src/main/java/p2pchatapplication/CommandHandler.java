package p2pchatapplication;

public class CommandHandler {
    public static void handle(String input, String myUsername) {
        if (input.equalsIgnoreCase("@Exit")) {
            System.out.println("👋 Exiting...");
            System.exit(0);
        } else if (input.equalsIgnoreCase("@Users")) {
            FriendManager.printActiveFriends();
        } else if (input.startsWith("@Add ")) {
            String newUser = input.substring(5).trim();
            UserManager.addFriend(newUser);
            FriendManager.discoverUser(newUser);
        } else if (input.startsWith("@")) {
            int spaceIndex = input.indexOf(" ");
            if (spaceIndex != -1) {
                String to = input.substring(1, spaceIndex);
                String msg = input.substring(spaceIndex + 1);
                ChatClient.sendMessage(myUsername, to, msg, 5000);
            } else {
                System.out.println("Invalid format. Use @Username message");
            }
        } else {
            System.out.println("Unknown command.");
        }
    }
}
