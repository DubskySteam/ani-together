package dev.dubsky.anitogether.util;

import java.io.*;
import java.net.*;
import java.util.Set;

import dev.dubsky.anitogether.player.MpvController;

public class StreamUtils {

    public static void cleanupMpv() {
        try {
            MpvController.getInstance().quit();
        } catch (IOException e) {
            System.err.println("MPV is already closed");
        }
    }

    public static void cleanupServer(ServerSocket serverSocket, Set<Socket> clients) {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            for (Socket client : clients) {
                if (!client.isClosed()) {
                    client.close();
                }
            }
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }

    public static void cleanup() {
        System.out.println("Performing general cleanup...");
        cleanupMpv();
    }
}
