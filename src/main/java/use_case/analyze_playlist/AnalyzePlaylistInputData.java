package use_case.analyze_playlist;

import com.google.gson.JsonArray;

/**
 * The Input Data structure for the Analyze Playlist use case.
 * It holds the raw data necessary to execute the use case logic,
 * which in this case is the combined string of song lyrics.
 */
public class AnalyzePlaylistInputData {
    private final String playlistId;
    private final JsonArray songs;
    private final String playlistname;

    // derive songs from playlist.songs
    public AnalyzePlaylistInputData(String playlistId, String playlistName, JsonArray songs) {
        this.playlistId = playlistId;
        this.songs = songs;
        this.playlistname = playlistName;
    }

    String getPlaylistId() {
        return playlistId;
    }

    String getPlaylistname() {
        return playlistname;
    }

    JsonArray getSongs() {
        return songs;
    }

    // TODO: Move your codes here (Suggestion: Just Delete them if they are only used in Interactor, since you can derive the lyrics from Interactor now)
    /*
    private final String combinedLyrics;

    /**
     * Constructs the Input Data object.
     * @param combinedLyrics The concatenated string of all lyrics to be analyzed.
     */

    /*
    public AnalyzePlaylistInputData(String combinedLyrics) {
        this.combinedLyrics = combinedLyrics;
    }

    // Getter
    public String getCombinedLyrics() {
        return combinedLyrics;
    }*/
}
