package data_access;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.SentimentResult;
import use_case.analyze_playlist.SentimentDataAccessInterface;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.Map;

/**
 * Concrete implementation of the SentimentDataAccessInterface that uses the Gemini API
 * to perform sentiment analysis on a block of lyrics.
 * NOTE: This class uses the built-in Java 11+ HttpClient and the Gson library for JSON parsing.
 * The analysis provides a descriptive "sentiment word" and "explanation."
 */
public class DBGeminiDataAccessObject implements SentimentDataAccessInterface {

    // Removed static API_KEY field. Key is now loaded from environment variable.
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent";
    private final HttpClient httpClient;
    private final Gson gson;
    private final String apiKey; // Instance field to hold the key

    public DBGeminiDataAccessObject() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();

        // Load the key from the environment variable.
        // We use a specific, secure name for the variable.
        String key = System.getenv("GEMINI_API_KEY");

        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "The GEMINI_API_KEY environment variable is not set or is empty. " +
                            "Please configure it in your IntelliJ Run Configuration or system environment."
            );
        }
        this.apiKey = key;
    }

    /**
     * Constructs the system instruction that forces the Gemini model to respond
     * with a JSON object matching the required schema for Sentiment analysis.
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
        // --- 1. Build the API Request Payload ---
        String systemInstruction = createSystemInstruction();
        String userQuery = "Analyze the sentiment of this playlist's lyrics and explain your finding: \n\n--- LYRICS ---\n\n" + combinedLyrics;

        // Escape quotes and newlines for JSON string formatting
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


        // --- 2. Execute the HTTP Request ---
        HttpRequest request = HttpRequest.newBuilder()
                // Use the loaded apiKey instance variable here
                .uri(URI.create(API_URL + "?key=" + this.apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                // If the API returns an error status code
                String error = "Gemini API Error (Status: " + response.statusCode() + "): " + response.body();
                throw new IOException(error);
            }

            // --- 3. Parse the API Response ---
            return parseGeminiResponse(response.body());

        } catch (InterruptedException e) {
            // Handle thread interruption
            Thread.currentThread().interrupt();
            throw new IOException("API request interrupted.", e);
        }
    }

    /**
     * Parses the complex nested JSON response from the Gemini API and extracts the
     * structured Sentiment JSON, then converts it into a SentimentResult entity.
     * @param jsonResponse The raw JSON string from the API.
     * @return A SentimentResult entity (now focused on descriptive Sentiment).
     * @throws IOException If the JSON structure is unexpected or parsing fails.
     */
    private SentimentResult parseGeminiResponse(String jsonResponse) throws IOException {
        try {
            // Step 1: Parse the top-level API response structure
            Map<String, Object> responseMap = gson.fromJson(jsonResponse, new TypeToken<Map<String, Object>>() {}.getType());

            // Step 2: Navigate to the content part containing the generated JSON text
            java.util.List<?> candidates = (java.util.List<?>) responseMap.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new IOException("API response missing 'candidates'. Response: " + jsonResponse);
            }
            Map<String, Object> candidate = (Map<String, Object>) candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            java.util.List<?> parts = (java.util.List<?>) content.get("parts");
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

            // Step 4: Create and return the Entity, using 0.0 and emptyMap for the old fields
            return new SentimentResult(
                    sentimentWord,
                    sentimentExplanation
            );

        } catch (Exception e) {
            // Catch parsing errors (e.g., API didn't return perfect JSON)
            throw new IOException("Failed to parse Gemini API response. Check API key and response format. Error: " + e.getMessage(), e);
        }
    }
}