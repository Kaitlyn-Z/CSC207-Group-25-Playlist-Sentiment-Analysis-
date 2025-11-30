package use_case.analyze_playlist;

import com.google.gson.JsonArray;

public interface SpotifyPlaylistDataAccessInterface {
    /**
     * Get Lyrics from playlist's songs method.
     * @param songs JsonArray get from Playlist
     * @return A JsonArray of songs' title, artist and lyrics
     */
    JsonArray getLyrics(JsonArray songs);
}
