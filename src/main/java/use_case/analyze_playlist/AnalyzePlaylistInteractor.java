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
        if (playlist.getSongs().isEmpty()) {
            analyzePlaylistPresenter.prepareFailView("Selected playlist is empty.");
            return;
        }

        final JsonArray songsInfo = spotifyPlaylistDataAccessObject.getLyrics(playlist.getSongs());
        if (songsInfo.size() == 0) {
            analyzePlaylistPresenter.prepareFailView("No lyrics were found for the songs in this playlist.");
            return;
        }

        // Section2: get analysis from the lyrics
        StringBuilder combinedLyrics = new StringBuilder();
        for (int i = 0; i < songsInfo.size(); i++) {
            combinedLyrics.append(songsInfo.get(i).getAsJsonObject().get("lyrics").getAsString());
            combinedLyrics.append("\n\n---\n\n"); // Separator for clarity
        }

        String lyrics = combinedLyrics.toString();

        if (lyrics.trim().isEmpty()) {
            analyzePlaylistPresenter.prepareFailView("The found lyrics were empty.");
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
            analyzePlaylistPresenter.prepareFailView("Failed to connect to the sentiment analysis service: " + e.getMessage());
        } catch (Exception e) {
            analyzePlaylistPresenter.prepareFailView("An unexpected error occurred during analysis: " + e.getMessage());
        }
    }
}
