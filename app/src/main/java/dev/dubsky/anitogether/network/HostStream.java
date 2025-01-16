package dev.dubsky.anitogether.network;

import dev.dubsky.anitogether.Main;
import dev.dubsky.anitogether.ui.MenuManager;
import dev.dubsky.anitogether.ui.Color;
import dev.dubsky.anitogether.ui.Menu;
import dev.dubsky.anitogether.util.StreamUtils;
import dev.dubsky.anitogether.util.config;

import java.io.*;
import java.net.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class HostStream {

    private static final Set<Socket> clients = ConcurrentHashMap.newKeySet();
    private static ServerSocket serverSocket;
    private static boolean isPaused = false;

    public static void start() {
        try {
            initializeServer();
            displayHostInfo();

            Thread acceptThread = createClientAcceptThread();
            acceptThread.start();

            waitForClients();
            handleStreamControlMenu();
        } catch (IOException | URISyntaxException e) {
            System.out.println(Color.RED + "Hosting stream failed: " + e.getMessage() + Color.RESET);
        } finally {
            cleanupResources();
        }
    }

    private static void initializeServer() throws IOException {
        serverSocket = new ServerSocket(config.PORT);
    }

    private static void displayHostInfo() throws IOException, URISyntaxException {
        String localIp = InetAddress.getLocalHost().getHostAddress();
        String publicIp = fetchPublicIp();
        Menu.displayHostingInfo(localIp, publicIp);
    }

    private static String fetchPublicIp() throws IOException, URISyntaxException {
        URI ipService = new URI("http://checkip.amazonaws.com");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(ipService.toURL().openStream()))) {
            return in.readLine();
        }
    }

    private static Thread createClientAcceptThread() {
        return new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    Socket client = serverSocket.accept();
                    clients.add(client);
                    Menu.displayClientConnected(client.getInetAddress().getHostAddress(), clients.size());
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        System.err.println("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        });
    }

    private static void waitForClients() throws IOException {
        Menu.displayWaitingForClients();
        System.in.read();

        if (clients.isEmpty()) {
            Menu.displayNoClientsMessage();
        }

        String streamUrl = getStreamUrl();
        broadcastMessage("START:" + streamUrl);
        StreamUtils.playStream(streamUrl);
    }

    private static String getStreamUrl() throws IOException {
        Menu.getStreamUrl();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        return reader.readLine();
    }

    private static void handleStreamControlMenu() {
        MenuManager hostMenu = new MenuManager("Host Control Menu");

        hostMenu.addOption("1", "Toggle Pause", choice -> togglePause());
        hostMenu.addOption("2", "Seek to Position", choice -> seekToPosition());
        hostMenu.addOption("3", "Stop Stream", choice -> {
            quit();
        });
        hostMenu.addOption("4", "Load New Stream URL", choice -> loadNewUrl());

        while (true) {
            Menu.clearMenu();
            hostMenu.display();
            String choice = hostMenu.getUserInput();
            hostMenu.executeChoice(choice);

            if ("3".equals(choice)) {
                break;
            }
        }
    }

    private static void togglePause() {
        try {
            String command = isPaused ? "PAUSE" : "PLAY";
            if (isPaused) {
                Main.mpv.pause();
            } else {
                Main.mpv.unpause();
            }
            broadcastMessage(command);
            isPaused = !isPaused;
        } catch (IOException e) {
            System.out.println(Color.RED + "Error toggling pause: " + e.getMessage() + Color.RESET);
        }
    }

    private static void seekToPosition() {
        try {
            System.out.print(Color.YELLOW + "Enter seek position in seconds: " + Color.RESET);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            int seconds = Integer.parseInt(reader.readLine());

            Main.mpv.seekTo(seconds);
            broadcastMessage("SEEK:" + seconds);
            System.out.println("Seeked to " + seconds + " seconds.");
        } catch (IOException | NumberFormatException e) {
            System.out.println(Color.RED + "Error seeking position: " + e.getMessage() + Color.RESET);
        }
    }

    private static void quit() {
        broadcastMessage("QUIT");
        cleanupResources();
    }

    private static void loadNewUrl() {
        try {
            String newUrl = getStreamUrl();
            Main.mpv.loadStream(newUrl);
            broadcastMessage("NEW_STREAM:" + newUrl);
            System.out.println("New stream loaded: " + newUrl);
        } catch (IOException e) {
            System.out.println(Color.RED + "Error loading new stream URL: " + e.getMessage() + Color.RESET);
        }
    }

    private static void broadcastMessage(String message) {
        for (Socket client : clients) {
            try (PrintWriter clientOut = new PrintWriter(client.getOutputStream(), true)) {
                clientOut.println(message);
            } catch (IOException e) {
                System.err.println("Error broadcasting message to client: " + e.getMessage());
            }
        }
    }

    private static void cleanupResources() {
        StreamUtils.cleanupMpv();
        StreamUtils.cleanupServer(serverSocket, clients);
    }
}
