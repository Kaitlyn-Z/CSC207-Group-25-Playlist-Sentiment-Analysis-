package use_case.analyze_playlist;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import entity.PlaylistFactory;
import entity.SentimentResult;
import entity.SentimentResultFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnalyzePlaylistInteractorTest {

    //Tests of Section1: get lyrics from the selected playlist
    @Test
    void getLyricsSuccessTest() {
        String playlist = "[" + "{\"artist\":\"Rihanna\",\"title\":\"Diamonds\"}" + "]";
        JsonArray songs = JsonParser.parseString(playlist).getAsJsonArray();
        AnalyzePlaylistInputData inputData = new AnalyzePlaylistInputData("id", "MyPlaylist", songs);
        PlaylistFactory playlistFactory = new PlaylistFactory();

        SpotifyPlaylistDataAccessInterface mockPlaylistDAO = new SpotifyPlaylistDataAccessInterface() {
            @Override
            public JsonArray getLyrics(JsonArray passedSongs) {

                assertEquals(1, passedSongs.size());
                assertEquals("Rihanna", passedSongs.get(0).getAsJsonObject().get("artist").getAsString());
                assertEquals("Diamonds", passedSongs.get(0).getAsJsonObject().get("title").getAsString());

                String songsInfo = "["
                        + "{\"artist\":\"Rihanna\",\"title\":\"Diamonds\",\"lyrics\":\"Shine bright like a diamond\"}"
                        + "]";
                JsonArray result = JsonParser.parseString(songsInfo).getAsJsonArray();

                return result;
            }
        };

        final boolean[] sentimentCalled = {false};
        SentimentDataAccessInterface mockSentimentDAO = new SentimentDataAccessInterface() {
            @Override
            public SentimentResult analyzeSentiment(String lyrics) {
                sentimentCalled[0] = true;
                return new SentimentResult("Positive", "Happy playlist");
            }
        };

        AnalyzePlaylistOutputBoundary mockPresenter = new AnalyzePlaylistOutputBoundary() {
            @Override public void prepareSuccessView(AnalyzePlaylistOutputData outputData) {
                assertEquals("Positive", outputData.getOverallCategory());
                assertEquals("Happy playlist", outputData.getSummaryText());
            }

            @Override
            public void prepareFailView(String error) {fail("Should not fail");}
        };

        AnalyzePlaylistInteractor interactor = new AnalyzePlaylistInteractor(
                playlistFactory,
                new SentimentResultFactory(),
                mockSentimentDAO,
                mockPresenter,
                mockPlaylistDAO
        );
        interactor.execute(inputData);
        assertTrue(sentimentCalled[0]);
        assertTrue(true);
    }

    @Test
    void failureEmptyPlaylistTest() {
        JsonArray emptyList = new JsonArray();
        AnalyzePlaylistInputData inputData = new AnalyzePlaylistInputData("id", "MyPlaylist", emptyList);
        PlaylistFactory playlistFactory = new PlaylistFactory();

        SpotifyPlaylistDataAccessInterface mockPlaylistDAO = new SpotifyPlaylistDataAccessInterface() {
            @Override
            public JsonArray getLyrics(JsonArray songs) {
                fail("getLyrics should NOT be called when playlist is empty");
                return null;
            }
        };

        SentimentDataAccessInterface mockSentimentDAO = new SentimentDataAccessInterface() {
            @Override
            public entity.SentimentResult analyzeSentiment(String lyrics) {
                fail("Sentiment should not run");
                return null;
            }
        };

        AnalyzePlaylistOutputBoundary mockPresenter = new AnalyzePlaylistOutputBoundary() {
            @Override
            public void prepareSuccessView(AnalyzePlaylistOutputData data) {fail("Should not succeed");}

            @Override
            public void prepareFailView(String error) {assertEquals("Selected playlist is empty", error);}
        };

        AnalyzePlaylistInteractor interactor = new AnalyzePlaylistInteractor(
                playlistFactory,
                new SentimentResultFactory(),
                mockSentimentDAO,
                mockPresenter,
                mockPlaylistDAO
        );
        interactor.execute(inputData);
    }

    @Test
    void failureNoLyricsFoundTest() {
        PlaylistFactory playlistFactory = new PlaylistFactory();
        String playlist = "[" + "{\"artist\":\"DNE\",\"title\":\"DNE\"}" + "]";
        JsonArray songs = JsonParser.parseString(playlist).getAsJsonArray();
        AnalyzePlaylistInputData inputData = new AnalyzePlaylistInputData("id", "MyPlaylist", songs);

        SpotifyPlaylistDataAccessInterface mockPlaylistDAO = new SpotifyPlaylistDataAccessInterface() {
            @Override
            public JsonArray getLyrics(JsonArray songs) {return new JsonArray();}
        };

        SentimentDataAccessInterface mockSentimentDAO = new SentimentDataAccessInterface() {
            @Override
            public entity.SentimentResult analyzeSentiment(String lyrics) {
                fail("Sentiment should not run");
                return null;
            }
        };

        AnalyzePlaylistOutputBoundary mockPresenter = new AnalyzePlaylistOutputBoundary() {
            @Override
            public void prepareSuccessView(AnalyzePlaylistOutputData data) {fail("Should not succeed");}

            @Override
            public void prepareFailView(String error) {assertEquals("No lyrics found", error);}
        };

        AnalyzePlaylistInteractor interactor = new AnalyzePlaylistInteractor(
                playlistFactory,
                new SentimentResultFactory(),
                mockSentimentDAO,
                mockPresenter,
                mockPlaylistDAO
        );
        interactor.execute(inputData);
    }

    //Tests of Section2: get analysis from the lyrics

}


