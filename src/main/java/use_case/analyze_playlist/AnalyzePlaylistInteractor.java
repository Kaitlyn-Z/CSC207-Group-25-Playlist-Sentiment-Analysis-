package use_case.analyze_playlist;

import com.google.gson.JsonArray;
import entity.Playlist;
import entity.PlaylistFactory;
import entity.SentimentResult;
import entity.SentimentResultFactory;

import java.io.IOException;

/**
 * The Interactor handles the core business logic for the 'Analyze Playlist' use case.
 * It takes input data, uses the Data Access Object (DAO) to get the sentiment,
 * and passes the result to the Presenter.
 */
public class AnalyzePlaylistInteractor implements AnalyzePlaylistInputBoundary {
    private final SentimentDataAccessInterface sentimentDataAccessObject;
    private final AnalyzePlaylistOutputBoundary analyzePlaylistPresenter;
    private final PlaylistFactory playlistFactory;
    private final SentimentResultFactory sentimentResultFactory;
    private final SpotifyPlaylistDataAccessInterface spotifyPlaylistDataAccessObject;
    private final data_access.AnalysisStatsDataAccessObject analysisStatsDataAccessObject; // New field

    /**
     * Constructs the interactor with its dependencies.
     * @param analyzePlaylistPresenter AnalyzePlaylistOutputBoundary
     * @param playlistFactory PlaylistFactory
     * @param sentimentDataAccessObject SentimentDataAccessInterface
     * @param sentimentResultFactory SentimentResultFactory
     * @param spotifyPlaylistDataAccessObject SpotifyPlaylistDataAccessInterface
     * @param analysisStatsDataAccessObject AnalysisStatsDataAccessObject // New parameter
     */
    public AnalyzePlaylistInteractor(PlaylistFactory playlistFactory,
                                     SentimentResultFactory sentimentResultFactory,
                                     SentimentDataAccessInterface sentimentDataAccessObject,
                                     AnalyzePlaylistOutputBoundary analyzePlaylistPresenter,
                                     SpotifyPlaylistDataAccessInterface spotifyPlaylistDataAccessObject,
                                     data_access.AnalysisStatsDataAccessObject analysisStatsDataAccessObject) { // Modified constructor
        this.sentimentDataAccessObject = sentimentDataAccessObject;
        this.analyzePlaylistPresenter = analyzePlaylistPresenter;
        this.playlistFactory = playlistFactory;
        this.sentimentResultFactory = sentimentResultFactory;
        this.spotifyPlaylistDataAccessObject = spotifyPlaylistDataAccessObject;
        this.analysisStatsDataAccessObject = analysisStatsDataAccessObject; // Initialize new field
    }

    @Override
    public void execute(AnalyzePlaylistInputData inputData) {
        analysisStatsDataAccessObject.incrementAnalyzedPlaylistsCount(); // Increment count every time execute is called

        String lyrics = inputData.getCombinedLyrics();

        if (lyrics == null || lyrics.trim().isEmpty()) {
            analyzePlaylistPresenter.prepareFailView("Please enter some lyrics to analyze.");
            return;
        }

        try {
            SentimentResult result = sentimentDataAccessObject.analyzeSentiment(lyrics);

            AnalyzePlaylistOutputData outputData = new AnalyzePlaylistOutputData(
                    result.getSentimentWord(),
                    result.getSentimentExplanation()
            );

            analyzePlaylistPresenter.prepareSuccessView(outputData);
            // Removed increment call from here

        } catch (IOException e) {
            // Handle API or network errors
            analyzePlaylistPresenter.prepareFailView("Failed to connect to the sentiment analysis service: " + e.getMessage());
        } catch (Exception e) {
            // Catch any unexpected runtime errors
            analyzePlaylistPresenter.prepareFailView("An unexpected error occurred during analysis: " + e.getMessage());
        }
    }
}
