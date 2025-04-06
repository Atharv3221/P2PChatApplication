package p2pchatapplication;

import java.util.Scanner;

public class Main {
    private static String myUsername;
    public static String getMyUsername() { return myUsername; }

    public static void main(String[] args) {
        myUsername = UserManager.initializeUser();

        new Thread(() -> new ChatServer(5000).start()).start();

        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        FriendManager.discoverAllFriends();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            CommandHandler.handle(input, myUsername);
        }
    }
}
