package dev.dubsky.anitogether.network;

import dev.dubsky.anitogether.ui.Menu;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HostServer {

    private final int port;
    private ServerSocket serverSocket;
    private final ClientManager clientManager;

    public HostServer(int port, ClientManager clientManager) {
        this.port = port;
        this.clientManager = clientManager;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);

        new Thread(this::acceptClients).start();
    }

    public void stop() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
        clientManager.cleanupClients();
    }

    private void acceptClients() {
        while (!serverSocket.isClosed()) {
            try {
                Socket client = serverSocket.accept();
                clientManager.addClient(client);
                Menu.displayClientConnected(client.getInetAddress().toString(), clientManager.getClientCount());
            } catch (IOException e) {
                if (!serverSocket.isClosed()) {
                    System.err.println("Error accepting client: " + e.getMessage());
                }
            }
        }
    }
}
