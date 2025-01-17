package dev.dubsky.anitogether.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Scanner;

import dev.dubsky.anitogether.Main;
import dev.dubsky.anitogether.config.ConfigData;

public class Menu {

    private static final Scanner scanner = new Scanner(System.in);

    public static void clearMenu() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void showSettingsMenu() {
        MenuManager settingsMenu = new MenuManager("Settings Menu");
        ConfigData config = Main.configManager.getConfig();

        settingsMenu.addOption("1", "Change port", choice -> {
            System.out.print(Color.YELLOW + "Enter the new port number: " + Color.RESET);
            int newPort = Integer.parseInt(scanner.nextLine().trim());
            config.setPORT(newPort);
        });
        settingsMenu.addOption("2", "Toggle autoplay", choice -> {
            config.setAUTOPLAY(!config.isAUTOPLAY());
        });
        settingsMenu.addOption("3", "Save and return", choice -> {
            try {
                Main.configManager.saveConfig();
                System.out.println(Color.GREEN + "Settings saved." + Color.RESET);
            } catch (Exception e) {
                System.out.println(Color.RED + "Error saving config: " + e.getMessage() + Color.RESET);
            }
        });

        while (true) {
            clearMenu();
            System.out.println(Color.CYAN + "=== Settings ===" + Color.RESET);
            System.out.println("PORT: " + config.getPORT());
            System.out.println("AUTOPLAY: " + config.isAUTOPLAY());
            settingsMenu.display();
            String choice = settingsMenu.getUserInput();
            settingsMenu.executeChoice(choice);
            if (choice.equals("3")) {
                break;
            }
        }
    }

    public static void displayHostingInfo() {
        clearMenu();
        String localIp = "Unavailable";
        try {
            localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String publicIp = "Unavailable";
        try {
            publicIp = fetchPublicIp();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        System.out.println(Color.CYAN + "\n=== Hosting Information ===" + Color.RESET);
        System.out.println("Local IP: " + Color.GREEN+ localIp + Color.RESET);
        System.out.println("Public IP: " + Color.GREEN+ publicIp + Color.RESET);
        System.out.println(Color.YELLOW + "Share the public IP with your clients to join the stream." + Color.RESET);
    }

    private static String fetchPublicIp() throws IOException, URISyntaxException {
        URI ipService = new URI("http://checkip.amazonaws.com");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(ipService.toURL().openStream()))) {
            return in.readLine();
        }
    }

    public static void displayClientConnected(String clientIp, int clientCount) {
        System.out.println(Color.GREEN+ "Client connected: " + clientIp + Color.RESET);
        System.out.println("Total clients connected: " + clientCount);
    }

    public static void displayWaitingForClients() {
        System.out.println(Color.YELLOW + "Waiting for clients to connect..." + Color.RESET);
        System.out.println("Press ENTER to start the stream.");
        scanner.nextLine();
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
