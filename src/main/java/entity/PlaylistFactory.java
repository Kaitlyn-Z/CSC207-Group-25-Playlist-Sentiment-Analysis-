package entity;

import com.google.gson.JsonArray;

public class PlaylistFactory {
    /**
     * Create playlist method.
     * @param playlistId the unique identifier for the playlist
     * @param playlistName the display name of the playlist
     * @param songs the list of songs contained in the playlist
     * @return Playlist
     */
    public Playlist create(String playlistId, String playlistName, JsonArray songs) {
        return new Playlist(playlistId, playlistName, songs);
    }
}
