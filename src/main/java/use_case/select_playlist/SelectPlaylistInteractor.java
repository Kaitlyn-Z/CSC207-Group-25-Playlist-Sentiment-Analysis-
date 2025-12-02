package use_case.select_playlist;

import entity.Playlist;
import interface_adapter.logged_in.LoggedInViewModel;

public class SelectPlaylistInteractor implements SelectPlaylistInputBoundary {

    private final LoggedInViewModel loggedInViewModel;
    private final SelectPlaylistOutputBoundary presenter;

    public SelectPlaylistInteractor(LoggedInViewModel loggedInViewModel,
                                    SelectPlaylistOutputBoundary presenter) {
        this.loggedInViewModel = loggedInViewModel;
        this.presenter = presenter;
    }

    @Override
    public void execute(SelectPlaylistInputData inputData) {
        final var playlists = loggedInViewModel.getPlaylists();

        Playlist selected = null;
        for (Playlist p : playlists) {
            if (p.getPlaylistId().equals(inputData.getPlaylistId())) {
                selected = p;
                break;
            }
        }

        if (selected == null) {
            presenter.prepareFailView("Playlist not found.");
            return;
        }

        final SelectPlaylistOutputData outputData = new SelectPlaylistOutputData(selected, "Selected playlist: "
                + selected.getPlaylistName());

        presenter.prepareSuccessView(outputData);
    }
}
