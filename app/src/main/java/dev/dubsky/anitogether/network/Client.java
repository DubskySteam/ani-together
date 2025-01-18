package dev.dubsky.anitogether.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import dev.dubsky.anitogether.config.Params;
import dev.dubsky.anitogether.player.MpvController;
import dev.dubsky.anitogether.ui.Color;
import dev.dubsky.anitogether.ui.Menu;
import dev.dubsky.anitogether.ui.MenuManager;
import dev.dubsky.anitogether.util.StreamUtils;

public class Client {

    private static Client instance;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private Client() {}

    public static synchronized Client getInstance() {
        if (instance == null) {
            instance = new Client();
        }
        return instance;
    }

    public void start() {
        try {
            String HOST_IP = Menu.getHostIp();
            socket = new Socket(HOST_IP, Params.PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Connected to the server.");

            new Thread(this::listenForMessages).start();

            Menu.clearMenu();
            MenuManager clientControlMenu = new MenuManager("Client Control Menu");
            clientControlMenu.addOption("1", "End watching session", choice -> {
                System.out.println("Ending watching session...");
                cleanup();
            });

            while (true) {
                Menu.clearMenu();
                clientControlMenu.display();
                String choice = clientControlMenu.getUserInput();
                clientControlMenu.executeChoice(choice);
                if (choice.equals("1")) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to connect to the server: " + e.getMessage());
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.equals("QUIT")) {
                    System.out.println(Color.RED + "Stream ended. Exiting..." + Color.RESET);
                    StreamUtils.cleanup();
                    System.exit(0);
                } else {
                    processCommand(message);
                }
            }
        } catch (IOException e) {
            System.err.println("Connection lost: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void processCommand(String message) {
        try {
            if (message.startsWith("START|")) {
                String[] parts = message.substring(6).split("\\|", 2);
                if (parts.length == 2) {
                    String streamUrl = parts[0];
                    String subtitleUrl = parts[1];
                    MpvController.getInstance().startMpv(streamUrl, subtitleUrl);
                } else {
                    System.err.println("Invalid START message format: " + message);
                }            
            } else if (message.equals("PAUSE")) {
                MpvController.getInstance().pause();
            } else if (message.equals("RESUME")) {
                MpvController.getInstance().unpause();
            } else if (message.startsWith("SEEK|")) {
                int seconds = Integer.parseInt(message.substring(5));
                MpvController.getInstance().seekTo(seconds);
            } else if (message.equals("STOP")) {
                StreamUtils.cleanup();
            } else if (message.equals("QUIT")) {
                System.out.println(Color.RED + "Stream ended. Exiting..." + Color.RESET);
                StreamUtils.cleanup();
                cleanup();
            } else if (message.startsWith("NEW|")) {
                StreamUtils.cleanup();
                String[] parts = message.substring(4).split("\\|", 2);
                if (parts.length == 2) {
                    String streamUrl = parts[0];
                    String subtitleUrl = parts[1];
                    MpvController.getInstance().startMpv(streamUrl, subtitleUrl);
                } else {
                    System.err.println("Invalid START message format: " + message);
                }            
            } else {
                System.out.println(Color.YELLOW + "Unknown command received: " + message + Color.RESET);
            }
        } catch (Exception e) {
            System.out.println(Color.RED + "Error processing command: " + e.getMessage() + Color.RESET);
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void cleanup() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            System.err.println("Error cleaning up resources: " + e.getMessage());
        }
    }
}