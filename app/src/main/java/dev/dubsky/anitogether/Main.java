package dev.dubsky.anitogether;

import dev.dubsky.anitogether.network.HostStream;
import dev.dubsky.anitogether.network.JoinStream;
import dev.dubsky.anitogether.player.MpvController;
import dev.dubsky.anitogether.ui.Menu;
import dev.dubsky.anitogether.ui.MenuManager;
import dev.dubsky.anitogether.util.StreamUtils;

public class Main {

    public static MpvController mpv;

    public static void main(String[] args) {
        mpv = new MpvController();

        MenuManager mainMenu = new MenuManager("AniTogether Main Menu");

        mainMenu.addOption("1", "Watch Anime", choice -> {});
        mainMenu.addOption("2", "Host Stream", choice -> HostStream.start());
        mainMenu.addOption("3", "Join Stream", choice -> {
            String hostIp = getHostIp();
            JoinStream.start(hostIp);
        });
        mainMenu.addOption("4", "Exit", choice -> {
            StreamUtils.cleanup();
            System.exit(0);
        });

        while (true) {
            Menu.clearMenu();
            mainMenu.display();
            String choice = mainMenu.getUserInput();
            mainMenu.executeChoice(choice);
        }
    }

    private static String getHostIp() {
        System.out.print("Enter the host's IP address: ");
        try (java.util.Scanner scanner = new java.util.Scanner(System.in)) {
            return scanner.nextLine().trim();
        }
    }
}
