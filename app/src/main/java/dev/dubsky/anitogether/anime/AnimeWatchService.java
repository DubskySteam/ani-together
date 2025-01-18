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
            StreamUtils.cleanupMpv();
            playEpisode(streamData);
            
            while (MpvController.getInstance().isPlaying()) {
                MenuManager postEpisodeMenu = new MenuManager("Anime Control");
                postEpisodeMenu.addOption("1", "Next episode", choice -> {
                    try {
                        List<String> nextEpisodeData = AnimeService.selectAnimeAndEpisode();
                        playEpisode(nextEpisodeData);
                    } catch (IOException e) {
                        System.err.println("Error selecting next episode: " + e.getMessage());
                    }
                });
                postEpisodeMenu.addOption("2", "Back to main menu", null);
                String choice = postEpisodeMenu.getUserInput();
                postEpisodeMenu.executeChoice(choice);
                if (choice.equals("2")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error watching anime: " + e.getMessage());
        }
    }

    public static void playEpisode(List<String> streamData) throws IOException {
        MpvController mpv = MpvController.getInstance();
        mpv.startMpv(streamData.get(0), streamData.get(1));
    }

}
