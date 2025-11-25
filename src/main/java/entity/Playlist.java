package entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * An entity representing a playlist
 */
public class Playlist {
    private String playlistName;
    private final String playlistId;
    private JsonArray songs; //expected type: [{"artist": artistName1, "title": songName1}, {"artist":name2, "title": title2}]
    private boolean selected;

    public Playlist(String playlistId, String playlistName, JsonArray songs) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.songs = songs;
        this.selected = false; // default: unselected
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public JsonArray getSongs() {return songs;}

    public String getPlaylistName() {return playlistName;}



}