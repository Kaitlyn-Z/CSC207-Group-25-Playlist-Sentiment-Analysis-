package use_case.analyze_playlist;

import entity.SentimentResult;
import java.io.IOException;

/**
 * Defines the contract for external services (like the Gemini API) to perform
 * sentiment analysis. This allows the core business logic (the Interactor)
 * to be independent of the specific data access implementation.
 */
public interface SentimentDataAccessInterface {

    /**
     * Calls an external service (e.g., Gemini API) to analyze the overall
     * sentiment of a provided block of text.
     *
     * @param combinedLyrics A single String containing the concatenated lyrics
     * of all songs in the playlist.
     * @return A SentimentResult entity containing the structured analysis.
     * @throws IOException If a network or API communication error occurs.
     */
    SentimentResult analyzeSentiment(String combinedLyrics) throws IOException;

    // NOTE: If you decide to add the Lyric API integration later, you might
    // rename this interface to DataAccessInterface and add a getLyrics() method here,
    // or create a separate LyricDataAccessInterface. For now, this is dedicated to Gemini's sentiment.
}