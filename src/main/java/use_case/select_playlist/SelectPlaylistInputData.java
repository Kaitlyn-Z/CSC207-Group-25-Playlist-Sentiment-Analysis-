package use_case.select_playlist;

public class SelectPlaylistInputData {
    private final String playlistId;
    private final String playlistName;

    public SelectPlaylistInputData(String playlistId, String playlistName) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }
}
