package interface_adapter.analysis;

import entity.SentimentResult;

//TODO: Since you don't need to input lyrics from outside, these codes may need to be modified.
/**
 * The mutable state data structure for the Analysis View.
 */
public class AnalysisState {
    private String lyrics = "";
    private boolean isLoading = false;
    private SentimentResult result = null;
    private String errorMessage = null;

    // Constructor (Copy constructor for thread-safe state management)
    public AnalysisState(AnalysisState copy) {
        lyrics = copy.lyrics;
        isLoading = copy.isLoading;
        result = copy.result;
        errorMessage = copy.errorMessage;
    }

    // Default constructor
    public AnalysisState() {}

    // Getters
    public String getLyrics() {
        return lyrics;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public SentimentResult getResult() {
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Setters
    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setResult(SentimentResult result) {
        this.result = result;
        this.errorMessage = null; // Clear error on success
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        this.result = null; // Clear result on error
    }
}