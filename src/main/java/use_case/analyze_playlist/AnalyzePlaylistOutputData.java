package use_case.analyze_playlist;

import java.util.Map;

/**
 * The Output Data structure for the Analyze Playlist use case.
 * It holds the analysis results as primitive types ready for the Presenter to format.
 */
public class AnalyzePlaylistOutputData {

    private final String overallCategory;
    private final double numericalScore;
    private final String summaryText;
    private final Map<String, Double> sentimentBreakdown;

    /**
     * Constructs the Output Data object by extracting necessary primitives from the analysis result.
     *
     * @param overallCategory A high-level assessment of the sentiment.
     * @param numericalScore A quantitative measure of the sentiment (-1.0 to 1.0).
     * @param summaryText The full text analysis from the LLM.
     * @param sentimentBreakdown A map showing the distribution of sentiment categories.
     */
    public AnalyzePlaylistOutputData(
            String overallCategory,
            double numericalScore,
            String summaryText,
            Map<String, Double> sentimentBreakdown) {
        this.overallCategory = overallCategory;
        this.numericalScore = numericalScore;
        this.summaryText = summaryText;
        this.sentimentBreakdown = sentimentBreakdown;
    }

    // Getters for the Presenter
    public String getOverallCategory() {
        return overallCategory;
    }

    public double getNumericalScore() {
        return numericalScore;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public Map<String, Double> getSentimentBreakdown() {
        return sentimentBreakdown;
    }
}