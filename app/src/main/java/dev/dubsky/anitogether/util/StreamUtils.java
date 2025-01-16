package dev.dubsky.anitogether.util;

import java.io.*;
import java.net.*;
import java.util.Set;

import dev.dubsky.anitogether.Main;
import dev.dubsky.anitogether.player.MpvController;

public class StreamUtils {

    private static MpvController mpvController = new MpvController();

    public static void playStream(String streamUrl, String subtitleUrl) {
        try {
            Main.mpv.startMpv(streamUrl, subtitleUrl);
            Thread.sleep(2000);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error playing stream: " + e.getMessage());
        }
    }
    

    public static void cleanupMpv() {
        try {
            mpvController.quit();
        } catch (IOException e) {
            System.err.println("Error closing mpv: " + e.getMessage());
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
        try {
            Main.mpv.quit();
        } catch (IOException e) {
            System.err.println("Error closing mpv: " + e.getMessage());
        }
    }
    
}
