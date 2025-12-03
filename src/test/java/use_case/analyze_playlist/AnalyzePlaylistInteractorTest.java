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
                @Override
                public String getStringLyrics(JsonArray passedSongs) {
                    assertEquals(1, passedSongs.size());
                    assertEquals(
                            "Shine bright like a diamond",
                            passedSongs.get(0).getAsJsonObject().get("lyrics").getAsString()
                    );
                    return "Shine bright like a diamond\n";
                };
            };

            final boolean[] sentimentCalled = {false};
            SentimentDataAccessInterface mockSentimentDAO = new SentimentDataAccessInterface() {
                @Override
                public SentimentResult analyzeSentiment(String lyrics) {
                    assertEquals("Shine bright like a diamond\n", lyrics);
                    sentimentCalled[0] = true;
                    return new SentimentResult("Positive", "Happy playlist");
                }
            };

            AnalyzePlaylistOutputBoundary mockPresenter = new AnalyzePlaylistOutputBoundary() {
                @Override
                public void prepareSuccessView(AnalyzePlaylistOutputData outputData) {
                    assertEquals("Positive", outputData.getOverallCategory());
                    assertEquals("Happy playlist", outputData.getSummaryText());
                }

                @Override
                public void prepareFailView(String error) {
                    fail("Should not fail");
                }
            };

            AnalysisStatsDataAccessInterface mockStatsDAO = new AnalysisStatsDataAccessInterface() {
                @Override
                public Map<String, Integer> loadStats() {
                    return new HashMap<>(Map.of("analyzedPlaylistsCount", 0));
                }

                @Override
                public void saveStats(Map<String, Integer> stats) {}

                @Override
                public int getAnalyzedPlaylistsCount() {
                    return 0;
                }

                @Override
                public void incrementAnalyzedPlaylistsCount() {}
            };

            AnalyzePlaylistInteractor interactor = new AnalyzePlaylistInteractor(
                    playlistFactory,
                    new SentimentResultFactory(),
                    mockSentimentDAO,
                    mockPresenter,
                    mockPlaylistDAO,
                    mockStatsDAO
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
                @Override
                public String getStringLyrics(JsonArray songs) {
                    fail("getStringLyrics should Not be called");
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
                public void prepareSuccessView(AnalyzePlaylistOutputData data) {
                    fail("Should not succeed");
                }

                @Override
                public void prepareFailView(String error) {
                    assertEquals("Selected playlist is empty", error);
                }
            };

            AnalyzePlaylistInteractor interactor = new AnalyzePlaylistInteractor(
                    playlistFactory,
                    new SentimentResultFactory(),
                    mockSentimentDAO,
                    mockPresenter,
                    mockPlaylistDAO,
                    null
            );
            interactor.execute(inputData);
        }

        @Test
        void failureNoLyricsFoundTest() {
            PlaylistFactory playlistFactory = new PlaylistFactory();
            String playlist = "[" + "{\"artist\":\"DNE\",\"title\":\"DNE\"}," + "]";
            JsonArray songs = JsonParser.parseString(playlist).getAsJsonArray();
            AnalyzePlaylistInputData inputData = new AnalyzePlaylistInputData("id", "MyPlaylist", songs);

            SpotifyPlaylistDataAccessInterface mockPlaylistDAO = new SpotifyPlaylistDataAccessInterface() {
                @Override
                public JsonArray getLyrics(JsonArray songs) {
                    return new JsonArray();
                }
                @Override
                public String getStringLyrics(JsonArray songs) {
                    fail("getStringLyrics should NOT be called");
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
                public void prepareSuccessView(AnalyzePlaylistOutputData data) {
                    fail("Should not succeed");
                }

                @Override
                public void prepareFailView(String error) {
                    assertEquals("No lyrics found", error);
                }
            };

            AnalyzePlaylistInteractor interactor = new AnalyzePlaylistInteractor(
                    playlistFactory,
                    new SentimentResultFactory(),
                    mockSentimentDAO,
                    mockPresenter,
                    mockPlaylistDAO,
                    null
            );
            interactor.execute(inputData);
        }

        // TODO: finish section 2 tests
        //Tests of Section2: get analysis from the lyrics
        @Test
        void analysisSuccessTest() {
            String combinedLyrics = "Shine bright like a diamond\n";
            String playlist = "[" + "{\"artist\":\"Rihanna\",\"title\":\"Diamonds\"}" + "]";
            JsonArray songs = JsonParser.parseString(playlist).getAsJsonArray();
            AnalyzePlaylistInputData inputData = new AnalyzePlaylistInputData("id", "MyPlaylist", songs);

            SpotifyPlaylistDataAccessInterface mockPlaylistDAO = new SpotifyPlaylistDataAccessInterface() {
                @Override
                public JsonArray getLyrics(JsonArray songs) {
                    String songsInfo = "["
                            + "{\"artist\":\"Rihanna\",\"title\":\"Diamonds\",\"lyrics\":\"Shine bright like a diamond\"}"
                            + "]";
                    return JsonParser.parseString(songsInfo).getAsJsonArray();
                }
                @Override
                public String getStringLyrics(JsonArray songs) { return combinedLyrics; }
            };

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
                @Override
                public void prepareSuccessView(AnalyzePlaylistOutputData outputData) {
                    assertEquals("Positive", outputData.getOverallCategory());
                    assertEquals("Happy playlist", outputData.getSummaryText());
                }

                @Override
                public void prepareFailView(String error) {
                    fail("Should not fail: " + error);
                }
            };

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
                    new PlaylistFactory(),
                    new SentimentResultFactory(),
                    mockSentimentDAO,
                    mockPresenter,
                    mockPlaylistDAO, // Pass the mock DAO
                    mockAnalysisStatsDAO
            );
            interactor.execute(inputData);
            assertTrue(sentimentCalled[0]);
        }

        @Test
        void failureAnalysisOneTest() {
            String emptyLyrics = "";
            AnalyzePlaylistInputData inputData = new AnalyzePlaylistInputData("id", "playlist", new JsonArray());

            SpotifyPlaylistDataAccessInterface mockPlaylistDAO = new SpotifyPlaylistDataAccessInterface() {
                @Override
                public JsonArray getLyrics(JsonArray songs) { return new JsonArray(); }
                @Override
                public String getStringLyrics(JsonArray songs) { return emptyLyrics; }
            };

            SentimentDataAccessInterface mockSentimentDAO = (lyrics) -> {
                fail("Sentiment analysis should not be called for empty lyrics");
                return null;
            };

            AnalyzePlaylistOutputBoundary mockPresenter = new AnalyzePlaylistOutputBoundary() {
                @Override
                public void prepareSuccessView(AnalyzePlaylistOutputData data) {
                    fail("Should not succeed for empty lyrics");
                }

                @Override
                public void prepareFailView(String error) {
                    assertEquals("Selected playlist is empty", error);
                }
            };

            AnalyzePlaylistInteractor interactor = new AnalyzePlaylistInteractor(
                    new PlaylistFactory(),
                    new SentimentResultFactory(),
                    mockSentimentDAO,
                    mockPresenter,
                    mockPlaylistDAO,
                    new AnalysisStatsDataAccessObject("test_stats.json")
            );
            interactor.execute(inputData);
        }


    }



