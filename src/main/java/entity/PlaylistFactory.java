package entity;

import com.google.gson.JsonArray;

public class PlaylistFactory {
    public Playlist create(String playlistId, String playlistName, JsonArray songs) {
        return new Playlist(playlistId, playlistName, songs);
    }
}
