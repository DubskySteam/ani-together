package dev.dubsky.anitogether.network;

import dev.dubsky.anitogether.Main;
import dev.dubsky.anitogether.ui.MenuManager;
import dev.dubsky.anitogether.ui.Color;
import dev.dubsky.anitogether.util.StreamUtils;
import dev.dubsky.anitogether.util.config;

import java.io.*;
import java.net.*;

public class JoinStream {

    public static void start(String hostIp) {
        System.out.println(Color.CYAN + "Attempting to join stream at " + hostIp + ":" + config.PORT + "..." + Color.RESET);

        try (Socket clientSocket = new Socket()) {
            clientSocket.connect(new InetSocketAddress(hostIp, config.PORT), 5000);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            MenuManager waitingMenu = new MenuManager("Waiting for Stream to Start");
            waitingMenu.addOption("1", "Return to Main Menu", choice -> {
                try {
                    out.println("QUIT");
                    StreamUtils.cleanup();
                    System.exit(0);
                } catch (Exception e) {
                    System.out.println(Color.RED + "Error quitting: " + e.getMessage() + Color.RESET);
                }
            });
            waitingMenu.display();

            String message;
            while ((message = in.readLine()) != null) {
                processCommand(message);
            }

        } catch (SocketTimeoutException e) {
            System.out.println(Color.RED + "Connection timed out: " + e.getMessage() + Color.RESET);
        } catch (IOException e) {
            System.out.println(Color.RED + "Failed to join stream: " + e.getMessage() + Color.RESET);
        }
    }

    private static void processCommand(String message) {
        try {
            if (message.startsWith("START:")) {
                String streamUrl = message.substring(6);
                System.out.println(Color.GREEN + "Starting stream: " + streamUrl + Color.RESET);
                StreamUtils.playStream(streamUrl);

            } else if (message.equals("PAUSE")) {
                System.out.println(Color.YELLOW + "Stream paused." + Color.RESET);
                Main.mpv.pause();

            } else if (message.startsWith("SEEK:")) {
                int seconds = Integer.parseInt(message.substring(5));
                System.out.println(Color.CYAN + "Seeking to position: " + seconds + " seconds." + Color.RESET);
                Main.mpv.seekTo(seconds);

            } else if (message.equals("PLAY")) {
                System.out.println(Color.GREEN + "Stream resumed." + Color.RESET);
                Main.mpv.unpause();

            } else if (message.equals("QUIT")) {
                System.out.println(Color.RED + "Stream ended. Exiting..." + Color.RESET);
                StreamUtils.cleanup();
                System.exit(0);

            } else if (message.startsWith("NEW_STREAM:")) {
                String newUrl = message.substring(11);
                System.out.println(Color.GREEN + "Loading new stream: " + newUrl + Color.RESET);
                Main.mpv.loadStream(newUrl);

            } else {
                System.out.println(Color.YELLOW + "Unknown command received: " + message + Color.RESET);
            }
        } catch (Exception e) {
            System.out.println(Color.RED + "Error processing command: " + e.getMessage() + Color.RESET);
        }
    }
}
