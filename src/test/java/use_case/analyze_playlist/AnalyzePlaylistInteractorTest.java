package use_case.analyze_playlist;

import data_access.AnalysisStatsDataAccessObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import entity.PlaylistFactory;
import entity.SentimentResult;
import entity.SentimentResultFactory;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AnalyzePlaylistInteractorTest {

    //Tests of Section1: get lyrics from the selected playlist
    @Test
    void getLyricsSuccessTest() {
        String combinedLyrics = "Shine bright like a diamond";
        AnalyzePlaylistInputData inputData = new AnalyzePlaylistInputData(combinedLyrics);

        final boolean[] sentimentCalled = {false};
        SentimentDataAccessInterface mockSentimentDAO = new SentimentDataAccessInterface() {
            @Override
            public SentimentResult analyzeSentiment(String lyrics) {
                sentimentCalled[0] = true;
                assertEquals(combinedLyrics, lyrics); // Verify lyrics are passed correctly
                return new SentimentResult("Positive", "Happy playlist");
            }
        };

        AnalyzePlaylistOutputBoundary mockPresenter = new AnalyzePlaylistOutputBoundary() {
            @Override public void prepareSuccessView(AnalyzePlaylistOutputData outputData) {
                assertEquals("Positive", outputData.getOverallCategory());
                assertEquals("Happy playlist", outputData.getSummaryText());
            }

            @Override
            public void prepareFailView(String error) {fail("Should not fail: " + error);}
        };
        
        // Mock AnalysisStatsDataAccessObject
        AnalysisStatsDataAccessObject mockAnalysisStatsDAO = new AnalysisStatsDataAccessObject("test_stats.json") {
            @Override
            public void incrementAnalyzedPlaylistsCount() {
                // Do nothing for testing
            }
            @Override
            public int getAnalyzedPlaylistsCount() {
                return 0; // Always return 0 for testing
            }
            @Override
            public Map<String, Integer> loadStats() {
                return new HashMap<>(Map.of("analyzedPlaylistsCount", 0));
            }
            @Override
            public void saveStats(Map<String, Integer> stats) {
                // Do nothing for testing
            }
        };

        // No need for PlaylistFactory, SentimentResultFactory, SpotifyPlaylistDataAccessInterface here
        // as they are not directly used in this specific execution path anymore
        AnalyzePlaylistInteractor interactor = new AnalyzePlaylistInteractor(
                new PlaylistFactory(), // Dummy
                new SentimentResultFactory(), // Dummy
                mockSentimentDAO,
                mockPresenter,
                null, // SpotifyPlaylistDataAccessInterface is no longer directly used in this path
                mockAnalysisStatsDAO
        );
        interactor.execute(inputData);
        assertTrue(sentimentCalled[0]);
    }

    @Test
    void failureEmptyPlaylistTest() {
        String emptyLyrics = "";
        AnalyzePlaylistInputData inputData = new AnalyzePlaylistInputData(emptyLyrics);

        SpotifyPlaylistDataAccessInterface mockPlaylistDAO = null; // Not used in this path
        SentimentDataAccessInterface mockSentimentDAO = null; // Not used in this path

        AnalyzePlaylistOutputBoundary mockPresenter = new AnalyzePlaylistOutputBoundary() {
            @Override
            public void prepareSuccessView(AnalyzePlaylistOutputData data) {fail("Should not succeed");}

            @Override
            public void prepareFailView(String error) {assertEquals("Please enter some lyrics to analyze.", error);}
        };

        // Mock AnalysisStatsDataAccessObject (same as before)
        AnalysisStatsDataAccessObject mockAnalysisStatsDAO = new AnalysisStatsDataAccessObject("test_stats.json") {
            @Override
            public void incrementAnalyzedPlaylistsCount() { /* Do nothing */ }
            @Override
            public int getAnalyzedPlaylistsCount() { return 0; }
            @Override
            public Map<String, Integer> loadStats() { return new HashMap<>(Map.of("analyzedPlaylistsCount", 0)); }
            @Override
            public void saveStats(Map<String, Integer> stats) { /* Do nothing */ }
        };

        AnalyzePlaylistInteractor interactor = new AnalyzePlaylistInteractor(
                new PlaylistFactory(), // Dummy
                new SentimentResultFactory(), // Dummy
                mockSentimentDAO,
                mockPresenter,
                mockPlaylistDAO,
                mockAnalysisStatsDAO
        );
        interactor.execute(inputData);
    }

    @Test
    void failureNoLyricsFoundTest() {
        String emptyLyrics = "";
        AnalyzePlaylistInputData inputData = new AnalyzePlaylistInputData(emptyLyrics);

        SpotifyPlaylistDataAccessInterface mockPlaylistDAO = null; // Not used in this path
        SentimentDataAccessInterface mockSentimentDAO = null; // Not used in this path

        AnalyzePlaylistOutputBoundary mockPresenter = new AnalyzePlaylistOutputBoundary() {
            @Override
            public void prepareSuccessView(AnalyzePlaylistOutputData data) {fail("Should not succeed");}

            @Override
            public void prepareFailView(String error) {assertEquals("Please enter some lyrics to analyze.", error);}
        };

        // Mock AnalysisStatsDataAccessObject (same as before)
        AnalysisStatsDataAccessObject mockAnalysisStatsDAO = new AnalysisStatsDataAccessObject("test_stats.json") {
            @Override
            public void incrementAnalyzedPlaylistsCount() { /* Do nothing */ }
            @Override
            public int getAnalyzedPlaylistsCount() { return 0; }
            @Override
            public Map<String, Integer> loadStats() { return new HashMap<>(Map.of("analyzedPlaylistsCount", 0)); }
            @Override
            public void saveStats(Map<String, Integer> stats) { /* Do nothing */ }
        };

        AnalyzePlaylistInteractor interactor = new AnalyzePlaylistInteractor(
                new PlaylistFactory(), // Dummy
                new SentimentResultFactory(), // Dummy
                mockSentimentDAO,
                mockPresenter,
                mockPlaylistDAO,
                mockAnalysisStatsDAO
        );
        interactor.execute(inputData);
    }

    //Tests of Section2: get analysis from the lyrics

}


