package p2pchatapplication;

import java.io.*;
import java.net.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String msg;
            while ((msg = in.readLine()) != null) {
                JsonObject json = JsonParser.parseString(msg).getAsJsonObject();
                String type = json.has("type") ? json.get("type").getAsString() : "";

                if ("DISCOVER".equalsIgnoreCase(type)) {
                    String username = json.has("username") ? json.get("username").getAsString() : "";
                    if (UserManager.isFriend(username)) {
                        FriendManager.addActiveFriend(username, socket.getInetAddress().getHostAddress());
                    }
                } else if ("MESSAGE".equalsIgnoreCase(type)) {
                    String from = json.get("from").getAsString();
                    String message = json.get("message").getAsString();
                    System.out.println("Received: @" + from + " \"" + message + "\"");
                    ChatClient.appendToChatHistory("Received: @" + from + " \"" + message + "\"");
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected.");
        }
    }
}
