package dev.dubsky.anitogether.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import dev.dubsky.anitogether.anime.AnimeService;
import dev.dubsky.anitogether.config.Params;
import dev.dubsky.anitogether.player.MpvController;
import dev.dubsky.anitogether.ui.Menu;
import dev.dubsky.anitogether.ui.MenuManager;
import dev.dubsky.anitogether.util.StreamUtils;

public class Host {

    private static Host instance;
    private final Set<Socket> clients = ConcurrentHashMap.newKeySet();
    private ServerSocket serverSocket;

    private Host() {
    }

    public static synchronized Host getInstance() {
        if (instance == null) {
            instance = new Host();
        }
        return instance;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(Params.PORT);
            System.out.println("Server started on port " + Params.PORT);

            new Thread(this::acceptClients).start();

            Menu.displayHostingInfo();
            Menu.displayWaitingForClients();

            List<String> streamData = AnimeService.selectAnimeAndEpisode();

            broadcastMessage("START|" + streamData.get(0) + "|" + streamData.get(1));
            MpvController.getInstance().startMpv(streamData.get(0), streamData.get(1));

            MenuManager hostControlMenu = new MenuManager("Host Control Menu");

            hostControlMenu.addOption("1", "Pause stream", choice -> {
                broadcastMessage("PAUSE");
                try {
                    MpvController.getInstance().pause();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            hostControlMenu.addOption("2", "Resume stream", choice -> {
                broadcastMessage("RESUME");
                try {
                    MpvController.getInstance().unpause();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            hostControlMenu.addOption("3", "Seek", choice -> {
                System.out.print("Enter seek time (in seconds): ");
                int seekTime = Integer.parseInt(System.console().readLine());
                broadcastMessage("SEEK" + "|" + seekTime);
                try {
                    MpvController.getInstance().seekTo(seekTime);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            hostControlMenu.addOption("4", "New Anime", choice -> {
                try {
                    List<String> newStreamData = AnimeService.selectAnimeAndEpisode();
                    broadcastMessage("NEW|" + newStreamData.get(0) + "|" + newStreamData.get(1));
                    StreamUtils.cleanup();
                    MpvController.getInstance().startMpv(newStreamData.get(0), newStreamData.get(1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            hostControlMenu.addOption("5", "Stop stream", choice -> {
                broadcastMessage("STOP");
                StreamUtils.cleanup();
            });
            hostControlMenu.addOption("6", "Quit stream", choice -> {
                broadcastMessage("QUIT");
            });

            while (true) {
                Menu.clearMenu();
                hostControlMenu.display();
                String choice = hostControlMenu.getUserInput();
                hostControlMenu.executeChoice(choice);
                if (choice.equals("6")) {
                    StreamUtils.cleanup();
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    private void acceptClients() {
        while (!serverSocket.isClosed()) {
            try {
                Socket client = serverSocket.accept();
                clients.add(client);
                System.out.println("Client connected: " + client.getInetAddress());
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    System.err.println("Error accepting client: " + e.getMessage());
                }
            }
        }
    }

    public void broadcastMessage(String message) {
        for (Socket client : clients) {
            try {
                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                out.println(message);
            } catch (IOException e) {
                System.err.println("Error sending message to client: " + e.getMessage());
                clients.remove(client);
                closeClient(client);
            }
        }
    }

    private void closeClient(Socket client) {
        try {
            client.close();
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }

    public void stop() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            for (Socket client : clients) {
                closeClient(client);
            }
            clients.clear();
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }
}
