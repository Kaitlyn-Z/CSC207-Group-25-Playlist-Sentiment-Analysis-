package interface_adapter.analysis;

import com.google.gson.JsonArray;
import use_case.analyze_playlist.AnalyzePlaylistInputBoundary;
import use_case.analyze_playlist.AnalyzePlaylistInputData;

/**
 * The Controller. It takes input from the view and executes the corresponding use case.
 */
public class AnalysisController {
    private final AnalyzePlaylistInputBoundary analyzePlaylistInteractor;

    public AnalysisController(AnalyzePlaylistInputBoundary analyzePlaylistInteractor) {
        this.analyzePlaylistInteractor = analyzePlaylistInteractor;
    }

    /**
     * Execute method.
     * @param playlistId the unique identifier for the playlist
     * @param playlistName the display name of the playlist
     * @param songs the list of songs contained in the playlist
     */
    public void execute(String playlistId, String playlistName, JsonArray songs) {
        final AnalyzePlaylistInputData analyzePlaylistInputData = new AnalyzePlaylistInputData(playlistId, playlistName, songs);

        analyzePlaylistInteractor.execute(analyzePlaylistInputData);
    }
}
