package use_case.analyze_playlist;

/**
 * The Input Data structure for the Analyze Playlist use case.
 * It holds the raw data necessary to execute the use case logic,
 * which in this case is the combined string of song lyrics.
 */
public class AnalyzePlaylistInputData {

    private final String combinedLyrics;

    /**
     * Constructs the Input Data object.
     * @param combinedLyrics The concatenated string of all lyrics to be analyzed.
     */
    public AnalyzePlaylistInputData(String combinedLyrics) {
        this.combinedLyrics = combinedLyrics;
    }

    // Getter
    public String getCombinedLyrics() {
        return combinedLyrics;
    }
}