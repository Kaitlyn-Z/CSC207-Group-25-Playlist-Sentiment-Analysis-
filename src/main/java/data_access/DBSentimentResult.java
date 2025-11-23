package data_access;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.SentimentResult;
import entity.SentimentResultFactory;
import use_case.analyze_playlist.SentimentDataAccessInterface;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 * Concrete implementation of the SentimentDataAccessInterface that uses the Gemini API
 * to perform sentiment analysis on a block of lyrics.
 * This class delegates the creation of the final SentimentResult entity to a factory.
 */
public class DBSentimentResult implements SentimentDataAccessInterface {

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent";
    private final HttpClient httpClient;
    private final Gson gson;
    private final String apiKey;
    private final SentimentResultFactory sentimentResultFactory; // New Factory field

    /**
     * Constructs the data access object, loading the API key and accepting a factory dependency.
     *
     * @param resultFactory The factory responsible for creating SentimentResult entities.
     * @throws IllegalArgumentException if the GEMINI_API_KEY environment variable is not set.
     */
    public DBSentimentResult(SentimentResultFactory resultFactory) {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
        this.sentimentResultFactory = resultFactory;

        String key = System.getenv("GEMINI_API_KEY");

        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "The GEMINI_API_KEY environment variable is not set or is empty. " +
                            "Please add your API key to Run Configuration or system environment."
            );
        }
        this.apiKey = key;
    }

    /**
     * Constructs the system instruction that forces the Gemini model to respond
     * with a JSON object matching the required schema for Sentiment analysis.
     * @return The system instruction as a String.
     */
    private String createSystemInstruction() {
        return "You are a professional music analysis engine. Analyze the following combined lyrics from a playlist. " +
                "Your response MUST be a single JSON object that adheres strictly to the following schema. " +
                "Do not include any other text or explanation outside of the JSON object. " +
                "Identify the single most descriptive **sentiment word** or two that capture the overall feel of the lyrics.";
    }

    /**
     * Calls the Gemini API to analyze the sentiment of the provided lyrics.
     *
     * @param combinedLyrics A single String containing the concatenated lyrics.
     * @return A SentimentResult entity (now representing descriptive Sentiment Analysis).
     * @throws IOException If a network or API communication error occurs, or if parsing fails.
     */
    @Override
    public SentimentResult analyzeSentiment(String combinedLyrics) throws IOException {
        // ... (API Payload construction remains the same)
        String requestBody = getString(combinedLyrics);


        // --- 2. Execute the HTTP Request ---
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "?key=" + this.apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                String error = "Gemini API Error (Status: " + response.statusCode() + "): " + response.body();
                throw new IOException(error);
            }

            // --- 3. Parse the API Response ---
            return parseGeminiResponse(response.body());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("API request interrupted.", e);
        }
    }

    private String getString(String combinedLyrics) {
        String systemInstruction = createSystemInstruction();
        String userQuery = "Analyze the sentiment of this playlist's lyrics and explain your finding: \n\n--- LYRICS ---\n\n" + combinedLyrics;

        String escapedUserQuery = userQuery.replace("\"", "\\\"").replace("\n", "\\n");
        String escapedSystemInstruction = systemInstruction.replace("\"", "\\\"");

        String requestBody = String.format("""
            {
                "contents": [
                    { "parts": [ { "text": "%s" } ] }
                ],
                "systemInstruction": { "parts": [ { "text": "%s" } ] },
                "generationConfig": {
                    "responseMimeType": "application/json",
                    "responseSchema": {
                        "type": "OBJECT",
                        "properties": {
                            "sentimentWord": { "type": "STRING", "description": "A single word or two describing the sentiment." },
                            "sentimentExplanation": { "type": "STRING", "description": "A short paragraph explaining the sentiment." }
                        },
                        "required": ["sentimentWord", "sentimentExplanation"]
                    }
                }
            }
            """, escapedUserQuery, escapedSystemInstruction);
        return requestBody;
    }

    /**
     * Parses the complex nested JSON response from the Gemini API and extracts the
     * structured Sentiment JSON, then uses the factory to convert it into a SentimentResult entity.
     * @param jsonResponse The raw JSON string from the API.
     * @return A SentimentResult entity (now focused on descriptive Sentiment).
     * @throws IOException If the JSON structure is unexpected or parsing fails.
     */
    private SentimentResult parseGeminiResponse(String jsonResponse) throws IOException {
        try {
            // Step 1: Parse the top-level API response structure
            Map<String, Object> responseMap = gson.fromJson(jsonResponse, new TypeToken<Map<String, Object>>() {}.getType());

            // Step 2: Navigate to the content part containing the generated JSON text
            @SuppressWarnings("unchecked")
            List<Object> candidates = (List<Object>) responseMap.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new IOException("API response missing 'candidates'. Response: " + jsonResponse);
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> candidate = (Map<String, Object>) candidates.get(0);
            @SuppressWarnings("unchecked")
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            @SuppressWarnings("unchecked")
            List<Object> parts = (List<Object>) content.get("parts");
            @SuppressWarnings("unchecked")
            Map<String, Object> part = (Map<String, Object>) parts.get(0);
            String sentimentJsonString = (String) part.get("text");

            if (sentimentJsonString == null || sentimentJsonString.trim().isEmpty()) {
                throw new IOException("Gemini returned empty or null text content.");
            }

            // Step 3: Convert the generated JSON string to the final sentiment structure
            Map<String, Object> sentimentData = gson.fromJson(sentimentJsonString, new TypeToken<Map<String, Object>>() {}.getType());

            // Extract new Sentiment fields: sentimentWord and sentimentExplanation
            String sentimentWord = (String) sentimentData.getOrDefault("sentimentWord", "Undetermined");
            String sentimentExplanation = (String) sentimentData.getOrDefault("sentimentExplanation", "No explanation provided.");

            // Step 4: Use the Factory to create the entity
            return sentimentResultFactory.create(sentimentWord, sentimentExplanation);

        } catch (Exception e) {
            throw new IOException("Failed to parse Gemini API response. Check API key and response format. Error: " + e.getMessage(), e);
        }
    }
}