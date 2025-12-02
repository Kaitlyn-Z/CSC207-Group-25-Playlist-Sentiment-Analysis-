package use_case.select_playlist;

public interface SelectPlaylistOutputBoundary {
    /**
     * Success view.
     * @param outputData output data
     */
    void prepareSuccessView(SelectPlaylistOutputData outputData);

    /**
     * Fail view.
     * @param error error
     */
    void prepareFailView(String error);
}
