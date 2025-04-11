package p2pchatapplication;

import java.io.IOException;

public class ShowWeb {
    public static void main(String[] args) {
        try {
            new LocalWebServer();
        } catch (IOException e) {
            System.err.println("Failed to start web server: " + e.getMessage());
        }
    }
}