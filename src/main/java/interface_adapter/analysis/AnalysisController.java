package interface_adapter.analysis;

import use_case.analyze_playlist.AnalyzePlaylistInputBoundary;
import use_case.analyze_playlist.AnalyzePlaylistInputData;

/**
 * The Controller. It takes input from the view and executes the corresponding use case.
 */
public class AnalysisController {

    private final AnalyzePlaylistInputBoundary analyzePlaylistInteractor;
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
}