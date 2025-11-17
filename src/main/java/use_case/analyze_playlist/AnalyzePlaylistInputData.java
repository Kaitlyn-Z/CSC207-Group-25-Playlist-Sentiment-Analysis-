package use_case.analyze_playlist;

/**
 * The input data structure for the Interactor, carrying the required lyrics.
 */
public class AnalyzePlaylistInputData { // Renamed from AnalyzeLyricInputData to match file structure
    private final String lyrics;

    public AnalyzePlaylistInputData(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getLyrics() {
        return lyrics;
    }
}