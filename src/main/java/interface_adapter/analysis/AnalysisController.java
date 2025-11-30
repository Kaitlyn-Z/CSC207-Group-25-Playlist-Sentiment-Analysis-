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
     * Convenience overload for legacy callers that pass raw lyrics as a single string.
     * This wraps the lyrics into a JsonArray and delegates to the main execute method.
     * @param lyrics combined lyrics to analyze
     */
    public void execute(String lyrics) {
        JsonArray songs = new JsonArray();
        songs.add(lyrics);
        execute("combined-playlist-id", "Combined Playlist", songs);
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

    // TODO: Move codes (Suggestion: part 1 should be put in Presenter (you can check ca-lab), others should be deleted
    /*
    private final AnalysisViewModel analysisViewModel;

    public AnalysisController(
            AnalyzePlaylistInputBoundary analyzePlaylistInteractor,
            AnalysisViewModel analysisViewModel) {
        this.analyzePlaylistInteractor = analyzePlaylistInteractor;
        this.analysisViewModel = analysisViewModel;
    }

    /**
     * Initiates the sentiment analysis use case.
     * @param lyrics The text input from the user.
     */

    /*
    public void execute(String lyrics) {
        // 1. Update state to loading
        AnalysisState state = analysisViewModel.getState();
        state.setLyrics(lyrics);
        state.setLoading(true);
        state.setErrorMessage(null); // Clear previous error
        analysisViewModel.firePropertyChanged();

        // 2. Prepare Input Data
        AnalyzePlaylistInputData inputData = new AnalyzePlaylistInputData(lyrics);

        // 3. Execute the Interactor (which runs the API call asynchronously)
        analyzePlaylistInteractor.execute(inputData);
    }
    */
}
