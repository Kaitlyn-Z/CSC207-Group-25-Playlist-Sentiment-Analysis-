package entity;

public class PlaylistFactory {
    public Playlist create(String playlistId, String userId, String name, int songNumber) {
        return new Playlist(playlistId, userId, name, songNumber);
    }
}
