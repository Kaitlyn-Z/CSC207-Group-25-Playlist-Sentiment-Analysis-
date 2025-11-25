package entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Playlist entity that stores songs as a list of small maps:
 * [
 *   {"Adele": "Hello"},
 *   {"Jay Chou": "七里香"},
 *   {"Linkin Park": "Numb"}
 * ]
 *
 * Each map contains EXACTLY one pair:
 *   artist -> title
 */
public class Playlist {

    private String name;
    private final String playlistId;
    private final String userId;
    private int songNumber;

    // ★ Key part: Song list in the format Dylan wants
    private List<Map<String, String>> songs;

    // Keep one Gson object
    private static final Gson gson = new Gson();

    public Playlist(String playlistId, String userId, String name, int songNumber) {
        this.playlistId = playlistId;
        this.userId = userId;
        this.name = name;
        this.songNumber = songNumber;
        this.songs = new ArrayList<>();
    }

    // =============================
    //          BASIC GETTERS
    // =============================

    public String getPlaylistId() { return playlistId; }

    public String getUserId() { return userId; }

    public String getName() { return name; }

    public int getSongNumber() { return songNumber; }

    public List<Map<String, String>> getSongs() { return songs; }

    // =============================
    //           MUTATORS
    // =============================

    public void setName(String name) { this.name = name; }

    public void setSongs(List<Map<String, String>> songs) {
        this.songs = songs;
        this.songNumber = songs.size();
    }

    // =============================
    //     SONG MANIPULATION
    // =============================

    /** Add a new song in {artist : title} format */
    public void addSong(String artist, String title) {
        Map<String, String> entry = new HashMap<>();
        entry.put(artist, title);
        songs.add(entry);
        songNumber++;
    }

    /** Remove the first matching {artist : title} pair */
    public boolean removeSong(String artist, String title) {
        Iterator<Map<String, String>> it = songs.iterator();

        while (it.hasNext()) {
            Map<String, String> pair = it.next();
            if (pair.containsKey(artist) && pair.get(artist).equals(title)) {
                it.remove();
                songNumber--;
                return true;
            }
        }
        return false;
    }

    // =============================
    //      JSON SERIALIZATION
    // =============================

    /** Convert songs list to JSON string */
    public String songsToJson() {
        return gson.toJson(songs);
    }

    /** Load songs from a JSON string */
    public void loadSongsFromJson(String json) {
        Type listType = new TypeToken<List<Map<String, String>>>() {}.getType();
        this.songs = gson.fromJson(json, listType);

        if (songs == null) {
            songs = new ArrayList<>();
        }
        this.songNumber = songs.size();
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "name='" + name + '\'' +
                ", playlistId='" + playlistId + '\'' +
                ", userId='" + userId + '\'' +
                ", songs=" + songs +
                '}';
    }
}
