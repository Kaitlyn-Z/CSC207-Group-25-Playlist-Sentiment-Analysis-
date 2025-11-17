package use_case.analyze_playlist;

/**
 * The Output Boundary (Presenter Interface) used by the Interactor.
 */
public interface AnalyzePlaylistOutputBoundary {

    void prepareSuccessView(AnalyzePlaylistOutputData outputData);

    void prepareFailView(String error);
}