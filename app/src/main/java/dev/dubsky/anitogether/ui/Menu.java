package dev.dubsky.anitogether.ui;

import java.util.Scanner;

public class Menu {

    private static final Scanner scanner = new Scanner(System.in);

    public static void clearMenu() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void displayHostingInfo(String localIp, String publicIp) {
        clearMenu();
        System.out.println(Color.CYAN + "\n=== Hosting Information ===" + Color.RESET);
        System.out.println("Local IP: " + Color.GREEN+ localIp + Color.RESET);
        System.out.println("Public IP: " + Color.GREEN+ publicIp + Color.RESET);
        System.out.println(Color.YELLOW + "Share the public IP with your clients to join the stream." + Color.RESET);
    }

    public static void displayClientConnected(String clientIp, int clientCount) {
        System.out.println(Color.GREEN+ "Client connected: " + clientIp + Color.RESET);
        System.out.println("Total clients connected: " + clientCount);
    }

    public static void displayWaitingForClients() {
        System.out.println(Color.YELLOW + "Waiting for clients to connect..." + Color.RESET);
        System.out.println("Press ENTER to start the stream.");
    }

    public static void displayNoClientsMessage() {
        System.out.println(Color.RED + "No clients connected. Waiting for clients..." + Color.RESET);
    }

    public static void displayError(String message) {
        System.out.println(Color.RED + "Error: " + message + Color.RESET);
    }

    public static void displayHostIpError() {
        System.out.println(Color.RED + "Failed to determine public IP address." + Color.RESET);
    }

    public static String getUserChoice() {
        return scanner.nextLine().trim();
    }

    public static String getHostIp() {
        System.out.print(Color.YELLOW + "Enter the host's IP address: " + Color.RESET);
        return scanner.nextLine().trim();
    }

    public static String getStreamUrl() {
        System.out.print(Color.YELLOW + "Enter the stream URL: " + Color.RESET);
        return scanner.nextLine().trim();
    }

    public static void promptToStartStream() {
        System.out.println(Color.CYAN + "Press Enter to start the stream once all clients are connected." + Color.RESET);
        scanner.nextLine();
    }
}
