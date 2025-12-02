package use_case.select_playlist;

import entity.Playlist;
import interface_adapter.logged_in.LoggedInViewModel;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonArray;

import static org.junit.jupiter.api.Assertions.*;

class SelectPlaylistInteractorTest {

    @Test
    void successTest() {

        LoggedInViewModel viewModel = new LoggedInViewModel();

        JsonArray songs = new JsonArray();
        Playlist playlist = new Playlist("123", "MyPlaylist", songs);
        viewModel.setPlaylists(java.util.List.of(playlist));

        SelectPlaylistOutputBoundary successPresenter = new SelectPlaylistOutputBoundary() {
            @Override
            public void prepareSuccessView(SelectPlaylistOutputData outputData) {
                assertEquals("123", outputData.getSelectedPlaylist().getPlaylistId());
                assertEquals("MyPlaylist", outputData.getSelectedPlaylist().getPlaylistName());
                assertEquals("Selected playlist: MyPlaylist", outputData.getStatusMessage());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Unexpected failure.");
            }
        };

        SelectPlaylistInputBoundary interactor = new SelectPlaylistInteractor(viewModel, successPresenter);

        SelectPlaylistInputData inputData = new SelectPlaylistInputData("123", "MyPlaylist");

        interactor.execute(inputData);
    }


    @Test
    void playlistNotFoundFailureTest() {

        LoggedInViewModel viewModel = new LoggedInViewModel();
        viewModel.setPlaylists(java.util.List.of());

        SelectPlaylistOutputBoundary failurePresenter = new SelectPlaylistOutputBoundary() {
            @Override
            public void prepareSuccessView(SelectPlaylistOutputData outputData) {
                fail("Unexpected success.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Playlist not found.", error);
            }
        };

        SelectPlaylistInputBoundary interactor =
                new SelectPlaylistInteractor(viewModel, failurePresenter);

        SelectPlaylistInputData inputData =
                new SelectPlaylistInputData("9999", "Unknown");

        interactor.execute(inputData);
    }

    @Test
    void playlistExistsButIdDoesNotMatchTest() {

        LoggedInViewModel viewModel = new LoggedInViewModel();

        JsonArray songs = new JsonArray();
        Playlist playlist = new Playlist("123", "MyPlaylist", songs);
        viewModel.setPlaylists(java.util.List.of(playlist));

        SelectPlaylistOutputBoundary failurePresenter = new SelectPlaylistOutputBoundary() {
            @Override
            public void prepareSuccessView(SelectPlaylistOutputData outputData) {
                fail("Should not succeed.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Playlist not found.", error);
            }
        };

        SelectPlaylistInputBoundary interactor =
                new SelectPlaylistInteractor(viewModel, failurePresenter);

        SelectPlaylistInputData inputData =
                new SelectPlaylistInputData("999", "WrongName");

        interactor.execute(inputData);
    }
}

