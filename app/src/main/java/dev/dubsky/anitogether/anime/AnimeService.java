package dev.dubsky.anitogether.anime;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import dev.dubsky.anitogether.ui.ArrowKeySelector;

public class AnimeService {
    private static final AnimeFetcher apiClient = new AnimeFetcher();
    private static String lastWatchedAnimeId;
    private static int lastWatchedEpisodeIndex;

    public static List<String> selectAnimeAndEpisode() throws IOException {
        System.out.print("Enter anime name: ");
        String animeName = System.console().readLine();

        AnimeFetcher.SearchResult searchResult = apiClient.searchAnime(animeName, 1);
        List<String> animeNames = searchResult.animes.stream()
                .map(anime -> anime.name)
                .collect(Collectors.toList());

        String selectedAnime = ArrowKeySelector.select(animeNames, "Select an anime:");
        String animeId = searchResult.animes.get(animeNames.indexOf(selectedAnime)).id;

        AnimeFetcher.EpisodeList episodeList = apiClient.getEpisodeList(animeId);
        List<String> episodeNames = episodeList.episodes.stream()
                .map(episode -> "Episode " + episode.episodeNo + ": " + episode.name)
                .collect(Collectors.toList());

        String selectedEpisode = ArrowKeySelector.select(episodeNames, "Select an episode:");
        String episodeId = episodeList.episodes.get(episodeNames.indexOf(selectedEpisode)).episodeId;

        AnimeFetcher.StreamInfo streamInfo = apiClient.getStreamInfo(episodeId);
        String streamUrl = streamInfo.sources.get(0).url;
        String subtitleUrl = streamInfo.tracks.stream()
                .filter(track -> "captions".equals(track.kind) && "English".equals(track.label))
                .findFirst()
                .map(track -> track.file)
                .orElse(null);

        lastWatchedAnimeId = animeId;
        lastWatchedEpisodeIndex = episodeNames.indexOf(selectedEpisode);
        return List.of(streamUrl, subtitleUrl);
    }
    
    public static List<String> getNextEpisode() throws IOException {
        if (lastWatchedAnimeId == null) {
            throw new IllegalStateException("No previously watched anime");
        }

        AnimeFetcher.EpisodeList episodeList = apiClient.getEpisodeList(lastWatchedAnimeId);
        if (lastWatchedEpisodeIndex + 1 >= episodeList.episodes.size()) {
            return null; // No next episode available
        }

        lastWatchedEpisodeIndex++;
        AnimeFetcher.Episode nextEpisode = episodeList.episodes.get(lastWatchedEpisodeIndex);
        AnimeFetcher.StreamInfo streamInfo = apiClient.getStreamInfo(nextEpisode.episodeId);

        String streamUrl = streamInfo.sources.get(0).url;
        String subtitleUrl = streamInfo.tracks.stream()
                .filter(track -> "captions".equals(track.kind) && "English".equals(track.label))
                .findFirst()
                .map(track -> track.file)
                .orElse(null);

        return List.of(streamUrl, subtitleUrl);
    }

    public static boolean hasNextEpisode() throws IOException {
        if (lastWatchedAnimeId == null) {
            return false;
        }
        AnimeFetcher.EpisodeList episodeList = apiClient.getEpisodeList(lastWatchedAnimeId);
        return lastWatchedEpisodeIndex + 1 < episodeList.episodes.size();
    }
}