package interface_adapter.logged_in;

import use_case.select_playlist.SelectPlaylistInputBoundary;
import use_case.select_playlist.SelectPlaylistInputData;

public class SelectPlaylistController {

    private final SelectPlaylistInputBoundary interactor;

    public SelectPlaylistController(SelectPlaylistInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void execute(String playlistId, String playlistName) {
        interactor.execute(new SelectPlaylistInputData(playlistId, playlistName));
    }
}
