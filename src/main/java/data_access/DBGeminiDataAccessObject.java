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
 * * NOTE: This class requires Java 11+ for the HttpClient and the Gson library for JSON parsing.
 */
public class DBGeminiDataAccessObject implements SentimentDataAccessInterface {

    // IMPORTANT: In a real application, the API key should be loaded from a secure source (e.g., environment variable).
    // For demonstration, please replace this with your actual Gemini API Key.
    private static final String API_KEY = ""; // REPLACE WITH YOUR ACTUAL KEY
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent";
    private final HttpClient httpClient;
    private final Gson gson;

    public DBGeminiDataAccessObject() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * Constructs the system instruction that forces the Gemini model to respond
     * with a JSON object matching the required schema.
     */
    private String createSystemInstruction() {
        return "You are a professional music sentiment analysis engine. Analyze the following combined lyrics from a playlist. " +
                "Your response MUST be a single JSON object that adheres strictly to the following schema. " +
                "Do not include any other text or explanation outside of the JSON object. " +
                "The sentiment score is a double between -1.0 (most negative) and 1.0 (most positive). " +
                "The overall category must be one of: 'Highly Positive', 'Positive', 'Neutral', 'Negative', 'Highly Negative'.";
    }

    /**
     * Calls the Gemini API to analyze the sentiment of the provided lyrics.
     *
     * @param combinedLyrics A single String containing the concatenated lyrics.
     * @return A SentimentResult entity.
     * @throws IOException If a network or API communication error occurs, or if parsing fails.
     */
    @Override
    public SentimentResult analyzeSentiment(String combinedLyrics) throws IOException {
        if (API_KEY.isEmpty()) {
            throw new IOException("Gemini API Key is not set in DBGeminiDataAccessObject.java.");
        }

        // --- 1. Build the API Request Payload ---
        String systemInstruction = createSystemInstruction();
        String userQuery = "Analyze the sentiment of this playlist's lyrics: \n\n--- LYRICS ---\n\n" + combinedLyrics;

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
                            "overallCategory": { "type": "STRING" },
                            "summaryText": { "type": "STRING" },
                            "numericalScore": { "type": "NUMBER" },
                            "sentimentBreakdown": {
                                "type": "OBJECT",
                                "description": "Breakdown of sentiment categories and their scores (e.g., 'joy': 0.7)",
                                "additionalProperties": { "type": "NUMBER" }
                            }
                        }
                    }
                }
            }
            """, escapedUserQuery, escapedSystemInstruction);


        // --- 2. Execute the HTTP Request ---
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "?key=" + API_KEY))
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
     * structured sentiment JSON, then converts it into a SentimentResult entity.
     * @param jsonResponse The raw JSON string from the API.
     * @return A SentimentResult entity.
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

            // Step 3: Convert the generated JSON string to the final entity structure
            Map<String, Object> sentimentData = gson.fromJson(sentimentJsonString, new TypeToken<Map<String, Object>>() {}.getType());

            // Extract fields and ensure proper casting
            String overallCategory = (String) sentimentData.getOrDefault("overallCategory", "Neutral");
            String summaryText = (String) sentimentData.getOrDefault("summaryText", "No summary provided.");

            // Numerical values come as Double from Gson, use Number to handle potential floating point issues.
            double numericalScore = ((Number) sentimentData.getOrDefault("numericalScore", 0.0)).doubleValue();

            // Cast breakdown to the correct type
            Map<String, Double> breakdownMap;
            Object breakdownObj = sentimentData.getOrDefault("sentimentBreakdown", Collections.emptyMap());

            if (breakdownObj instanceof Map) {
                breakdownMap = (Map<String, Double>) breakdownObj;
            } else {
                breakdownMap = Collections.emptyMap();
            }

            // Step 4: Create and return the immutable Entity
            return new SentimentResult(overallCategory, numericalScore, summaryText, breakdownMap);

        } catch (Exception e) {
            // Catch parsing errors (e.g., API didn't return perfect JSON)
            throw new IOException("Failed to parse Gemini API response. Check API key and response format. Error: " + e.getMessage(), e);
        }
    }
}