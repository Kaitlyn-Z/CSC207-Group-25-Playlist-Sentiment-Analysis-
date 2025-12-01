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

    /**
     * Constructs the interactor with its dependencies.
     * @param analyzePlaylistPresenter AnalyzePlaylistOutputBoundary
     * @param playlistFactory PlaylistFactory
     * @param sentimentDataAccessObject SentimentDataAccessInterface
     * @param sentimentResultFactory SentimentResultFactory
     * @param spotifyPlaylistDataAccessObject SpotifyPlaylistDataAccessInterface
     */
    public AnalyzePlaylistInteractor(PlaylistFactory playlistFactory,
                                     SentimentResultFactory sentimentResultFactory,
                                     SentimentDataAccessInterface sentimentDataAccessObject,
                                     AnalyzePlaylistOutputBoundary analyzePlaylistPresenter,
                                     SpotifyPlaylistDataAccessInterface spotifyPlaylistDataAccessObject) {
        this.sentimentDataAccessObject = sentimentDataAccessObject;
        this.analyzePlaylistPresenter = analyzePlaylistPresenter;
        this.playlistFactory = playlistFactory;
        this.sentimentResultFactory = sentimentResultFactory;
        this.spotifyPlaylistDataAccessObject = spotifyPlaylistDataAccessObject;
    }

    @Override
    public void execute(AnalyzePlaylistInputData analyzePlaylistInputData) {
        // Section1: get lyrics from the selected playlist
        final Playlist playlist = playlistFactory.create(
                analyzePlaylistInputData.getPlaylistId(),
                analyzePlaylistInputData.getPlaylistname(),
                analyzePlaylistInputData.getSongs());
        if (playlist.getSongs().size() == 0) {
            analyzePlaylistPresenter.prepareFailView("Selected playlist is empty");
        }
        else {
            final JsonArray songsInfo = spotifyPlaylistDataAccessObject.getLyrics(playlist.getSongs());
            // randomly selected songs' info [{"artist": artistName, "title": titleName, "lyrics": lyrics}, {}...]
            if (songsInfo.size() == 0) {
                analyzePlaylistPresenter.prepareFailView("No lyrics found");
            }
            else {
                // Section2: get analysis from the lyrics
                // TODO: write codes here...

            }
        }
    }

    // TODO: merge your code into execute method (I have made runtimeException for no songs or no lyrics situations, you can find them in DBPlaylistDAO)

    /**
     * Executes the use case: fetches the sentiment and prepares the output view.
     * @param inputData The input containing the lyrics string.
     */
    /*
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
    */
}
