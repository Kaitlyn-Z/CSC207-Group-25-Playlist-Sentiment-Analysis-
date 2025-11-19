package use_case.analyze_playlist;

import java.util.Map;

/**
 * The Output Data structure for the Analyze Playlist use case.
 * It holds the analysis results as primitive types ready for the Presenter to format.
 */
public class AnalyzePlaylistOutputData {

    private final String overallCategory;
    private final String summaryText;

    /**
     * Constructs the Output Data object by extracting necessary primitives from the analysis result.
     *
     * @param overallCategory A high-level assessment of the sentiment.
     * @param summaryText The full text analysis from the LLM.
     */
    public AnalyzePlaylistOutputData(
            String overallCategory,
            String summaryText) {
        this.overallCategory = overallCategory;
        this.summaryText = summaryText;
    }

    // Getters for the Presenter
    public String getOverallCategory() {
        return overallCategory;
    }

    public String getSummaryText() {
        return summaryText;
    }
}