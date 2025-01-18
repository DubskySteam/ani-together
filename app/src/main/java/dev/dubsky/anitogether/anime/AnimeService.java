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
        List<String> animeNames = searchResult.data.animes.stream()
                .map(anime -> anime.name)
                .collect(Collectors.toList());

        String selectedAnime = ArrowKeySelector.select(animeNames, "Select an anime:");
        String animeId = searchResult.data.animes.get(animeNames.indexOf(selectedAnime)).id;

        AnimeFetcher.EpisodeList episodeList = apiClient.getEpisodeList(animeId);
        List<String> episodeNames = episodeList.data.episodes.stream()
                .map(episode -> "Episode " + episode.number + ": " + episode.title)
                .collect(Collectors.toList());

        String selectedEpisode = ArrowKeySelector.select(episodeNames, "Select an episode:");
        String episodeId = episodeList.data.episodes.get(episodeNames.indexOf(selectedEpisode)).episodeId;

        AnimeFetcher.StreamInfo streamInfo = apiClient.getStreamInfo(episodeId);
        String streamUrl = streamInfo.data.sources.get(0).url;
        String subtitleUrl = streamInfo.data.tracks.stream()
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
        if (lastWatchedEpisodeIndex + 1 >= episodeList.data.totalEpisodes) {
            return null; // No next episode available
        }

        lastWatchedEpisodeIndex++;
        AnimeFetcher.Episode nextEpisode = episodeList.data.episodes.get(lastWatchedEpisodeIndex);
        AnimeFetcher.StreamInfo streamInfo = apiClient.getStreamInfo(nextEpisode.episodeId);
        String streamUrl = streamInfo.data.sources.get(0).url;
        String subtitleUrl = streamInfo.data.tracks.stream()
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
        return lastWatchedEpisodeIndex + 1 < episodeList.data.totalEpisodes;
    }
}
