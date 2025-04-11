
# Peer-to-Peer LAN Chat Application

A lightweight peer-to-peer (P2P) chat application designed for local area networks (LAN). This application enables real-time text communication between users connected to the same network, without requiring internet access or external servers.

## Features

- **Username Registration**: Users can set their username once, which is saved locally.
- **Auto IP Discovery**: Uses DISCOVER/RESPONSE packets to dynamically detect other active users on the network.
- **Real-Time Chat**: Supports sending and receiving messages in real time over TCP.
- **Online User Tracking**: Displays a list of currently online peers.
- **Modular Architecture**: Separate components for networking, message handling, and user interaction.

## Use Cases

- College labs and classrooms
- Office environments
- Hostels and dormitories
- LAN parties and gaming
- Emergency or backup communication setups
- Temporary event setups (workshops, hackathons)

## Project Structure

```
src/
├── ChatClient.java         # Sends messages to other users
├── ChatServer.java         # Listens for incoming messages
├── ClientHandler.java      # Handles incoming TCP messages
├── CommandHandler.java     # Parses and executes user commands
├── FriendManager.java      # Manages DISCOVER/RESPONSE logic
├── UserManager.java        # Manages username persistence
├── DiscoveryListener.java  # Listens for DISCOVER packets
├── ResponseListener.java   # Handles incoming RESPONSE packets
├── NetworkUtils.java       # Fetches local IP
```

## How It Works

1. On launch, the application prompts the user to set a username (stored locally).
2. It then starts broadcasting a `DISCOVER` message over the LAN.
3. Other users respond with a `RESPONSE`, indicating their presence and details.
4. The list of online users is updated and maintained dynamically.
5. Users can send messages using the console by selecting usernames from the online list.
6. Messages are sent using TCP sockets and handled by the `ClientHandler` on the receiving end.

## Prerequisites

- Java 8 or higher
- A shared LAN environment (Wi-Fi or Ethernet)

## Running the Application

1. Clone the repository.
2. Compile the Java source files:
   ```bash
   javac *.java
   ```
3. Run the application:
   ```bash
   java Main
   ```

## Future Enhancements

- Alias system for resolving conflicts between identical usernames.
- GUI support (e.g., Swing or JavaFX).
- File sharing and media support.
- Encryption for message security.

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details., 
