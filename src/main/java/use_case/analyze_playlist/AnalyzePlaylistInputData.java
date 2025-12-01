package use_case.analyze_playlist;

import com.google.gson.JsonArray;

/**
 * The Input Data structure for the Analyze Playlist use case.
 * It holds the raw data necessary to execute the use case logic,
 * which in this case is the combined string of song lyrics.
 */
public class AnalyzePlaylistInputData {
    private String playlistId;
    private JsonArray songs;
    private String playlistname;
    private String combinedLyrics;

    // derive songs from playlist.songs
    public AnalyzePlaylistInputData(String playlistId, String playlistName, JsonArray songs) {
        this.playlistId = playlistId;
        this.songs = songs;
        this.playlistname = playlistName;
    }

    /**
     * Constructs the Input Data object.
     * @param combinedLyrics The concatenated string of all lyrics to be analyzed.
     */
    public AnalyzePlaylistInputData(String combinedLyrics) {
        this.combinedLyrics = combinedLyrics;
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

    // Getter
    public String getCombinedLyrics() {
        return combinedLyrics;
    }
}
