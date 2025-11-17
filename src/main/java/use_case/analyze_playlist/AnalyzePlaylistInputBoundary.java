package use_case.analyze_playlist;

/**
 * The Input Boundary (Controller Interface) used by the Controller.
 */
public interface AnalyzePlaylistInputBoundary {

    void execute(AnalyzePlaylistInputData inputData);
}