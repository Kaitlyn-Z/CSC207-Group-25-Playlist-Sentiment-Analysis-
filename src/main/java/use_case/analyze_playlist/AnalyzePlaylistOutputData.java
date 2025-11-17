package use_case.analyze_playlist;

/**
 * The output data structure from the Interactor to the Presenter, wrapping the sentiment result.
 */
public class AnalyzePlaylistOutputData { // Renamed from AnalyzeLyricOutputData to match file structure
    private final SentimentResult result;

    public AnalyzePlaylistOutputData(SentimentResult result) {
        this.result = result;
    }

    public SentimentResult getResult() {
        return result;
    }
}