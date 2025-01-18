package dev.dubsky.anitogether;

import java.io.IOException;

import dev.dubsky.anitogether.anime.AnimeWatchService;
import dev.dubsky.anitogether.config.ConfigManager;
import dev.dubsky.anitogether.network.Client;
import dev.dubsky.anitogether.network.Host;
import dev.dubsky.anitogether.ui.Menu;
import dev.dubsky.anitogether.ui.MenuManager;
import dev.dubsky.anitogether.util.StreamUtils;

public class Main {

    public static ConfigManager configManager;
    public static void main(String[] args) {

        configManager = new ConfigManager();
        
        try {
            configManager.loadConfig();
            System.out.println("Config loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error handling configuration: " + e.getMessage());
        }

        MenuManager mainMenu = new MenuManager("AniTogether Main Menu");

        mainMenu.addOption("1", "Watch Anime", choice -> AnimeWatchService.watchAnime());
        mainMenu.addOption("2", "Host Stream", choice -> {
            Host.getInstance().start();
        });
        mainMenu.addOption("3", "Join Stream", choice -> {
            Client.getInstance().start();
        });
        mainMenu.addOption("4", "Settings", choice -> Menu.showSettingsMenu());
        mainMenu.addOption("5", "Exit", choice -> exit());

        while (true) {
            Menu.clearMenu();
            mainMenu.display();
            String choice = mainMenu.getUserInput();
            mainMenu.executeChoice(choice);
        }
    }

    private static void exit() {
        StreamUtils.cleanup();
        System.exit(0);
    }
}
