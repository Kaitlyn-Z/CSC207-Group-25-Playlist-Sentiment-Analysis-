package use_case.analyze_playlist;

/**
 * The Input Boundary (Controller Interface) used by the Controller.
 */
public interface AnalyzePlaylistInputBoundary {
    // analyzePlaylistInputData equals Playlist.songs here
    /**
     * Execute method.
     * @param analyzePlaylistInputData AnalyzePlaylistInputData
     */
    void execute(AnalyzePlaylistInputData analyzePlaylistInputData);
}
