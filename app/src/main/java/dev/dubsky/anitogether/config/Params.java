package dev.dubsky.anitogether.config;

/**
 * Contains the configuration parameters for the application.
 */
public class Params {

    /**
     * The port number to use for the server.
     */
    public static int BASE_PORT = 27015;

    /**
     * The base URL for the API, used to fetch Anime info and episode URLs.
     */
    public static String BASE_API_URL = "https://anime-api-git-main-dubskys-projects.vercel.app";

    /**
     * The path to the configuration file.
     */
    public static final String CONFIG = System.getProperty("os.name").toLowerCase().contains("win")
                ? System.getenv("APPDATA") + "\\ani-together\\config.json"
                : System.getProperty("user.home") + "/.ani-together/config.json";
    
}
