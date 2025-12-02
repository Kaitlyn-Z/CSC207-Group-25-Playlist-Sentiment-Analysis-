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
        viewModel.setPlaylists(java.util.List.of()); // 空

        // 2. presenter 用来测试 fail 分支
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

        // 3. Interactor
        SelectPlaylistInputBoundary interactor =
                new SelectPlaylistInteractor(viewModel, failurePresenter);

        // 4. 输入不存在 playlist 的 id
        SelectPlaylistInputData inputData =
                new SelectPlaylistInputData("9999", "Unknown");

        // 5. 执行 use case
        interactor.execute(inputData);
    }
}

