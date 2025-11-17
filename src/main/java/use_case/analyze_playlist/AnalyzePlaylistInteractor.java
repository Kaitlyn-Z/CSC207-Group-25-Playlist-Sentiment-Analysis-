package use_case.analyze_playlist;

import java.io.IOException;

/**
 * The main interactor for the lyric sentiment analysis use case.
 * It coordinates the data access object and the presenter.
 */
public class AnalyzePlaylistInteractor implements AnalyzePlaylistInputBoundary { // Using AnalyzePlaylist name from your file structure

    private final SentimentDataAccessInterface sentimentDataAccessObject;
    private final AnalyzePlaylistOutputBoundary analyzePlaylistPresenter;

    // Assuming AnalyzePlaylistInteractor requires a SentimentDataAccessInterface
    public AnalyzePlaylistInteractor(
            SentimentDataAccessInterface sentimentDataAccessObject,
            AnalyzePlaylistOutputBoundary analyzePlaylistPresenter) {
        this.sentimentDataAccessObject = sentimentDataAccessObject;
        this.analyzePlaylistPresenter = analyzePlaylistPresenter;
    }

    /**
     * Executes the lyric analysis use case.
     * @param inputData Contains the lyrics string.
     */
    @Override
    public void execute(AnalyzePlaylistInputData inputData) {
        String lyrics = inputData.getLyrics();

        if (lyrics == null || lyrics.trim().isEmpty()) {
            analyzePlaylistPresenter.prepareFailView("Lyrics cannot be empty.");
            return;
        }

        // Run the blocking API call in a new thread to avoid freezing the Swing UI
        new Thread(() -> {
            try {
                // Call the external data access object (the Gemini API wrapper)
                SentimentResult result = sentimentDataAccessObject.analyzeSentiment(lyrics);

                // Package the successful result and pass it to the Presenter
                AnalyzePlaylistOutputData outputData = new AnalyzePlaylistOutputData(result);
                analyzePlaylistPresenter.prepareSuccessView(outputData);

            } catch (IOException e) {
                // Handle API/network failure
                analyzePlaylistPresenter.prepareFailView("Network/API Error: Could not reach the sentiment service. " + e.getMessage());
            } catch (InterruptedException e) {
                // Handle thread interruption
                Thread.currentThread().interrupt();
                analyzePlaylistPresenter.prepareFailView("Analysis process was interrupted.");
            } catch (Exception e) {
                // Catch any other unexpected errors (like bad parsing)
                analyzePlaylistPresenter.prepareFailView("Unexpected Analysis Error: " + e.getMessage());
            }
        }).start();
    }
}