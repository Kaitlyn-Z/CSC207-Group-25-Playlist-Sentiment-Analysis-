package use_case.select_playlist;

import entity.Playlist;

public class SelectPlaylistOutputData {
    private final Playlist selectedPlaylist;
    private final String statusMessage;

    public SelectPlaylistOutputData(Playlist selectedPlaylist, String statusMessage) {
        this.selectedPlaylist = selectedPlaylist;
        this.statusMessage = statusMessage;
    }

    public Playlist getSelectedPlaylist() {
        return selectedPlaylist;
    }

    public String getStatusMessage() {
        return statusMessage; }
}
