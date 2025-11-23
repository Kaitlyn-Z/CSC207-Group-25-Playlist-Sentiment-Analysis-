package use_case.analyze_playlist;

import entity.SentimentResult;

import java.io.IOException;

/**
 * The Interactor handles the core business logic for the 'Analyze Playlist' use case.
 * It takes input data, uses the Data Access Object (DAO) to get the sentiment,
 * and passes the result to the Presenter.
 */
public class AnalyzePlaylistInteractor implements AnalyzePlaylistInputBoundary {

    final SentimentDataAccessInterface sentimentDataAccessObject;
    final AnalyzePlaylistOutputBoundary analyzePlaylistPresenter;

    /**
     * Constructs the interactor with its dependencies.
     * @param sentimentDataAccessObject The DAO for getting sentiment analysis (e.g., Gemini API).
     * @param analyzePlaylistPresenter The presenter for outputting the results to the ViewModel.
     */
    public AnalyzePlaylistInteractor(
            SentimentDataAccessInterface sentimentDataAccessObject,
            AnalyzePlaylistOutputBoundary analyzePlaylistPresenter) {
        this.sentimentDataAccessObject = sentimentDataAccessObject;
        this.analyzePlaylistPresenter = analyzePlaylistPresenter;
    }

    /**
     * Executes the use case: fetches the sentiment and prepares the output view.
     * @param inputData The input containing the lyrics string.
     */
    @Override
    public void execute(AnalyzePlaylistInputData inputData) {
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

        } catch (IOException e) {
            // Handle API or network errors
            analyzePlaylistPresenter.prepareFailView("Failed to connect to the sentiment analysis service: " + e.getMessage());
        } catch (Exception e) {
            // Catch any unexpected runtime errors
            analyzePlaylistPresenter.prepareFailView("An unexpected error occurred during analysis: " + e.getMessage());
        }
    }
}