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

public class AnimeFetcher {
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public SearchResult searchAnime(String query, int page) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = String.format("%s/aniwatch/search?keyword=%s&page=%d", Params.BASE_API_URL, encodedQuery, page);
        String jsonResponse = executeGetRequest(url);
        return gson.fromJson(jsonResponse, SearchResult.class);
    }

    public EpisodeList getEpisodeList(String animeId) throws IOException {
        String url = String.format("%s/aniwatch/episodes/%s", Params.BASE_API_URL, animeId);
        String jsonResponse = executeGetRequest(url);
        return gson.fromJson(jsonResponse, EpisodeList.class);
    }

    public StreamInfo getStreamInfo(String episodeId) throws IOException {
        String url = String.format("%s/aniwatch/episode-srcs?id=%s", Params.BASE_API_URL, episodeId);
        System.out.println("Trying to call api with URL: " + url);
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
        public List<Anime> animes;
        public List<PopularAnime> mostPopularAnimes;
        public int currentPage;
        public boolean hasNextPage;
        public int totalPages;
        public List<String> genres;
    }

    public static class Anime {
        public String id;
        public String name;
        public String img;
        public Episodes episodes;
        public String duration;
        public boolean rated;
    }

    public static class PopularAnime {
        public String id;
        public String name;
        public String category;
        public String img;
        public Episodes episodes;
    }

    public static class Episodes {
        public int eps;
        public int sub;
        public int dub;
    }

    public static class EpisodeList {
        public int totalEpisodes;
        public List<Episode> episodes;
    }

    public static class Episode {
        public String name;
        public int episodeNo;
        public String episodeId;
        public boolean filler;
    }

    public static class StreamInfo {
        public List<Track> tracks;
        public Intro intro;
        public Outro outro;
        public List<StreamSource> sources;
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
    
    public static class StreamSource {
        public String url;
        public String type;
    }
    
}