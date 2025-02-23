package dev.dubsky.anitogether.anime;

import java.io.IOException;
import java.util.List;

import dev.dubsky.anitogether.player.MpvController;
import dev.dubsky.anitogether.ui.MenuManager;
import dev.dubsky.anitogether.util.StreamUtils;

public class AnimeWatchService {

    public static void watchAnime() {
        try {
            List<String> streamData = AnimeService.selectAnimeAndEpisode();
            playEpisode(streamData);
            MenuManager postEpisodeMenu = new MenuManager("Anime Control");
            postEpisodeMenu.addOption("1", "Next episode", choice -> {
                try {
                    List<String> nextEpisodeData = AnimeService.getNextEpisode();
                    playEpisode(nextEpisodeData);
                } catch (IOException e) {
                    System.err.println("Error selecting next episode: " + e.getMessage());
                }
            });
            postEpisodeMenu.addOption("2", "Back to main menu", null);

            while (true) {
                postEpisodeMenu.display();
                String choice = postEpisodeMenu.getUserInput();
                postEpisodeMenu.executeChoice(choice);
                if (choice.equals("2")) {
                    break;
                }
            }
            StreamUtils.cleanupMpv();
        } catch (IOException e) {
            System.err.println("Error watching anime: " + e.getMessage());
        }
    }

    public static void playEpisode(List<String> streamData) throws IOException {
        StreamUtils.cleanupMpv();
        MpvController.getInstance().startMpv(streamData.get(0), streamData.get(1));
    }

}
