package p2pchatapplication;

/**
 * Handles user input commands for chat, friend management, and discovery.
 */
public class CommandHandler {
    private final ChatClient chatClient;
    private final FriendManager friendManager;

    /**
     * Constructs a new CommandHandler.
     *
     * @param chatClient     The ChatClient used to send messages.
     * @param friendManager  The FriendManager handling friend-related tasks.
     */
    public CommandHandler(ChatClient chatClient, FriendManager friendManager) {
        this.chatClient = chatClient;
        this.friendManager = friendManager;
    }

    /**
     * Processes a command entered by the user via console input.
     *
     * Supported commands:
     * - @Add <username>: Adds a new friend and starts discovery.
     * - @Users: Lists online friends.
     * - @<username> <message>: Sends a direct message to the friend.
     *
     * @param input The raw command line input entered by the user.
     */
    public void handleCommand(String input) {
        if (input.startsWith("@Add ")) {
            String username = input.substring(5).trim();
            if (friendManager.addFriend(username)) {
                System.out.println("[CommandHandler] Added user: @" + username);
                friendManager.broadcastDiscovery(username);
            } else {
                System.out.println("[CommandHandler] User already added or error occurred.");
            }

        } else if (input.equalsIgnoreCase("@Users")) {
            System.out.println("Online Users:");
            friendManager.getOnlineUsers().forEach((name, ip) ->
                System.out.println(" - @" + name + " [" + ip + "]"));

        } else if (input.startsWith("@")) {
            int spaceIdx = input.indexOf(' ');
            if (spaceIdx != -1) {
                String targetUsername = input.substring(1, spaceIdx);
                String message = input.substring(spaceIdx + 1).trim();

                String targetIp = friendManager.getIpOfUser(targetUsername);
                if (targetIp == null) {
                    System.out.println("[CommandHandler] User @" + targetUsername + " is offline or not known.");
                    return;
                }

                String senderUsername = UserManager.getMyUsername();
                chatClient.sendMessage(targetUsername, message, targetIp, senderUsername);
            } else {
                System.out.println("[CommandHandler] Invalid message format. Use: @Username message");
            }

        } else {
            System.out.println("Unknown command. Use @Add, @Users, or @Username message.");
        }
    }
}
