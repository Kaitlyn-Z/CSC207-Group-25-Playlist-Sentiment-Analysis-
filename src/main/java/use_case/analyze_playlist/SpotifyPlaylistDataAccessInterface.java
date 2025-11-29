package use_case.analyze_playlist;

import com.google.gson.JsonArray;

public interface SpotifyPlaylistDataAccessInterface {
    JsonArray getLyrics(JsonArray songs);
}