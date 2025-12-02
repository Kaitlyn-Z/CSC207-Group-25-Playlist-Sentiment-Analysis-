package use_case.analyze_playlist;

import com.google.gson.JsonArray;

/**
 * The Input Data structure for the Analyze Playlist use case.
 * It holds the raw data necessary to execute the use case logic,
 * which in this case is the combined string of song lyrics.
 */
public class AnalyzePlaylistInputData {
    private String playlistId;
    private String playlistName;
    private JsonArray songs;

    // derive songs from playlist.songs
    public AnalyzePlaylistInputData(String playlistId, String playlistName, JsonArray songs) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.songs = songs;
    }

    String getPlaylistId() {
        return playlistId;
    }

    String getPlaylistName() {
        return playlistName;
    }

    JsonArray getSongs() {
        return songs;
    }

}
