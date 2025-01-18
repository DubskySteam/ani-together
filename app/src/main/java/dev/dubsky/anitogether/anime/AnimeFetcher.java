package dev.dubsky.anitogether.anime;

import com.google.gson.Gson;
import dev.dubsky.anitogether.config.Params;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class AnimeFetcher {
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public SearchResult searchAnime(String query, int page) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = String.format("%s/api/v2/hianime/search?q=%s&page=%d", Params.BASE_API_URL, encodedQuery, page);
        String jsonResponse = executeGetRequest(url);
        return gson.fromJson(jsonResponse, SearchResult.class);
    }

    public EpisodeList getEpisodeList(String animeId) throws IOException {
        String url = String.format("%s/api/v2/hianime/anime/%s/episodes", Params.BASE_API_URL, animeId);
        String jsonResponse = executeGetRequest(url);
        return gson.fromJson(jsonResponse, EpisodeList.class);
    }

    public StreamInfo getStreamInfo(String episodeId) throws IOException {
        String url = String.format("%s/api/v2/hianime/episode/sources?animeEpisodeId=%s", 
                                   Params.BASE_API_URL, episodeId);
        String jsonResponse = executeGetRequest(url);
        return gson.fromJson(jsonResponse, StreamInfo.class);
    }

    private String executeGetRequest(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }

    public static class SearchResult {
        public boolean success;
        public SearchData data;
    }

    public static class SearchData {
        public List<Anime> animes;
        public List<PopularAnime> mostPopularAnimes;
        public int currentPage;
        public int totalPages;
        public boolean hasNextPage;
        public String searchQuery;
        public Map<String, List<String>> searchFilters;
    }

    public static class Anime {
        public String id;
        public String name;
        public String poster;
        public String duration;
        public String type;
        public String rating;
        public Episodes episodes;
    }

    public static class PopularAnime {
        public Episodes episodes;
        public String id;
        public String jname;
        public String name;
        public String poster;
        public String type;
    }

    public static class Episodes {
        public int sub;
        public int dub;
    }

    public static class EpisodeList {
        public boolean success;
        public EpisodeData data;
    }

    public static class EpisodeData {
        public int totalEpisodes;
        public List<Episode> episodes;
    }

    public static class Episode {
        public int number;
        public String title;
        public String episodeId;
        public boolean isFiller;
    }

    public static class StreamInfo {
        public boolean success;
        public StreamData data;
    }

    public static class StreamData {
        public List<Track> tracks;
        public Intro intro;
        public Outro outro;
        public List<Source> sources;
        public Integer anilistID;
        public Integer malID;
    }

    public static class Track {
        public String file;
        public String label;
        public String kind;
        public boolean isDefault;
    }

    public static class Intro {
        public int start;
        public int end;
    }

    public static class Outro {
        public int start;
        public int end;
    }

    public static class Source {
        public String url;
        public String type;
    }
}