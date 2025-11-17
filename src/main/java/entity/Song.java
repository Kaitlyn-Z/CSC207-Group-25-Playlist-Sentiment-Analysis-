package entity;

/**
 * Entity representing a single song track.
 * This class holds data fetched from Spotify (ID, title, artist)
 * and data retrieved later from Genius (lyrics).
 */
public class Song {
    private final String songId; // Internal ID for your system
    private final String spotifyTrackId; // ID used by Spotify
    private final String title;
    private final String artist;
    private String lyrics; // Can be set after fetching from Genius

    /**
     * Constructor used when initially fetching tracks from Spotify.
     */
    public Song(String songId, String spotifyTrackId, String title, String artist) {
        this.songId = songId;
        this.spotifyTrackId = spotifyTrackId;
        this.title = title;
        this.artist = artist;
        this.lyrics = ""; // Initialize lyrics as empty
    }
    // Getters
    public String getSongId() { return songId; }

    public String getSpotifyTrackId() { return spotifyTrackId; }

    public String getTitle() { return title; }

    public String getArtist() { return artist; }

    public String getLyrics() { return lyrics; }

    /**
     * Setter for the lyrics attribute, which is populated by the Use Case
     * after calling the Genius API (via the adapter).
     * * @param lyrics The full text of the song lyrics.
     */
    public void setLyrics(String lyrics) { this.lyrics = lyrics; }

    @Override
    public String toString() {
        return "Song{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", hasLyrics=" + (!lyrics.isEmpty()) +
                '}';
    }
}