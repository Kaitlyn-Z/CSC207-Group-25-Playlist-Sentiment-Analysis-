package interface_adapter.analysis;

import com.google.gson.JsonArray;
import entity.SentimentResult;

/**
 * The mutable state data structure for the Analysis View.
 */
public class AnalysisState {
    private String playlistName = "";
    private JsonArray songs = null;
    private boolean isLoading = false;
    private SentimentResult result = null;
    private String errorMessage = null;

    // Constructor (Copy constructor for thread-safe state management)
    public AnalysisState(AnalysisState copy) {
        playlistName = copy.playlistName;
        songs = copy.songs;
        isLoading = copy.isLoading;
        result = copy.result;
        errorMessage = copy.errorMessage;
    }

    // Default constructor
    public AnalysisState() {}

    // Getters
    public String getPlaylistName() {
        return playlistName;
    }

    public JsonArray getSongs() {
        return songs;
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
    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public void setSongs(JsonArray songs) {
        this.songs = songs;
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