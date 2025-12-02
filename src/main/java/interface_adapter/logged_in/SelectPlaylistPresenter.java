package interface_adapter.logged_in;

import use_case.select_playlist.SelectPlaylistOutputBoundary;
import use_case.select_playlist.SelectPlaylistOutputData;

public class SelectPlaylistPresenter implements SelectPlaylistOutputBoundary {

    private final LoggedInViewModel loggedInViewModel;

    public SelectPlaylistPresenter(LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void prepareSuccessView(SelectPlaylistOutputData outputData) {
        final var playlist = outputData.getSelectedPlaylist();

        loggedInViewModel.setSelectedPlaylist(playlist);
        loggedInViewModel.setStatusMessage(outputData.getStatusMessage());
    }

    @Override
    public void prepareFailView(String error) {
        loggedInViewModel.setStatusMessage(error);
    }
}
