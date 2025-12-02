package data_access;

import entity.SentimentResult;
import entity.SentimentResultFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;
import java.io.IOException; // Added import

import static org.junit.jupiter.api.Assertions.*;

public class DBSentimentResultDataAccessObjectTest {

    private DBSentimentResultDataAccessObject sentimentDAO;
    private SentimentResultFactory sentimentResultFactory;

    private String geminiApiKey;

    @BeforeEach
    void setUp() {
        sentimentResultFactory = new SentimentResultFactory();
        sentimentDAO = new DBSentimentResultDataAccessObject(sentimentResultFactory);
        geminiApiKey = System.getenv("GEMINI_API_KEY");
    }

    @Test
    void testAnalyzeSentiment_success() throws IOException {
        Assumptions.assumeTrue(geminiApiKey != null && !geminiApiKey.isBlank(), "GEMINI_API_KEY environment variable is not set. Skipping test."); // Changed
        
        String sampleLyrics = "This song is incredibly uplifting and makes me feel very happy. What a wonderful piece of music!";
        SentimentResult result = sentimentDAO.analyzeSentiment(sampleLyrics);

        assertNotNull(result, "SentimentResult should not be null");
        assertNotNull(result.getSentimentWord(), "Sentiment word should not be null");
        assertNotNull(result.getSentimentExplanation(), "Sentiment explanation should not be null");
        assertFalse(result.getSentimentWord().isBlank(), "Sentiment word should not be blank");
        assertFalse(result.getSentimentExplanation().isBlank(), "Sentiment explanation should not be blank");
    }

    @Test
    void testAnalyzeSentiment_emptyLyrics() throws IOException {
        Assumptions.assumeTrue(geminiApiKey != null && !geminiApiKey.isBlank(), "GEMINI_API_KEY environment variable is not set. Skipping test."); // Changed

        String emptyLyrics = "";
        SentimentResult result = sentimentDAO.analyzeSentiment(emptyLyrics);
        assertNull(result, "SentimentResult should be null for empty lyrics");
    }

    @Test
    void testAnalyzeSentiment_invalidLyrics() throws IOException {
        Assumptions.assumeTrue(geminiApiKey != null && !geminiApiKey.isBlank(), "GEMINI_API_KEY environment variable is not set. Skipping test."); // Changed

        String invalidLyrics = "alskdjflaskdjf laskdjfalksdjf";
        SentimentResult result = sentimentDAO.analyzeSentiment(invalidLyrics);
        assertNull(result, "SentimentResult should be null or handle gracefully for invalid lyrics (API might error)");
    }
}
