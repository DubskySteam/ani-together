package dev.dubsky.anitogether.network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {

    private final Set<Socket> clients = ConcurrentHashMap.newKeySet();

    public void addClient(Socket client) {
        clients.add(client);
    }

    public int getClientCount() {
        return clients.size();
    }

    public void broadcastMessage(String message) throws IOException {
        for (Socket client : clients) {
            try (PrintWriter clientOut = new PrintWriter(client.getOutputStream(), true)) {
                clientOut.println(message);
            }
        }
    }

    public void cleanupClients() {
        for (Socket client : clients) {
            try {
                client.close();
            } catch (IOException ignored) {
            }
        }
        clients.clear();
    }
}
