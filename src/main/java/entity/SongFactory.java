package entity;

public class SongFactory {
    public Song create(String songId, String spotifyTrackId, String title, String artist) {
        return new Song(songId, spotifyTrackId, title, artist);
    }
}
