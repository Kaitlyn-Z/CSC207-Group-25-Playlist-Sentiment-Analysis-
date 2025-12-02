package use_case.select_playlist;

public interface SelectPlaylistOutputBoundary {
    void prepareSuccessView(SelectPlaylistOutputData outputData);
    void prepareFailView(String error);
}
