package data_access;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import use_case.analyze_playlist.AnalysisStatsDataAccessInterface;

import java.util.HashMap;
import java.util.Map;

/**
 * Data access object for persisting and retrieving analysis statistics.
 * This class handles reading from and writing to a JSON file named `analysis_stats.json`.
 */
public class AnalysisStatsDataAccessObject implements AnalysisStatsDataAccessInterface {

    private final String filePath;
    private final Gson gson;

    /**
     * Constructs an AnalysisStatsDataAccessObject.
     * @param filePath The path to the JSON file where statistics will be stored.
     */
    public AnalysisStatsDataAccessObject(String filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Loads the analysis statistics from the JSON file.
     * If the file does not exist or is empty, it returns an initial state with 0 analyzed songs.
     * @return A map containing the statistics, e.g., {"analyzedSongsCount": 0}.
     */
    public Map<String, Integer> loadStats() {
        try (FileReader reader = new FileReader(filePath)) {
            Type type = new TypeToken<Map<String, Integer>>() {}.getType();
            Map<String, Integer> stats = gson.fromJson(reader, type);
            return stats != null ? stats : new HashMap<>(Map.of("analyzedPlaylistsCount", 0));
        } catch (IOException e) {
            // File not found or other IO error, return default stats
            return new HashMap<>(Map.of("analyzedPlaylistsCount", 0));
        }
    }

    /**
     * Saves the analysis statistics to the JSON file.
     * @param stats A map containing the statistics to save.
     */
    public void saveStats(Map<String, Integer> stats) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(stats, writer);
        } catch (IOException e) {
            System.err.println("Error saving analysis statistics: " + e.getMessage());
        }
    }

    /**
     * Convenience method to get the current count of analyzed playlists.
     * @return The number of analyzed playlists, or 0 if not found/error.
     */
    public int getAnalyzedPlaylistsCount() {
        Map<String, Integer> stats = loadStats();
        return stats.getOrDefault("analyzedPlaylistsCount", 0);
    }

    /**
     * Convenience method to increment and save the analyzed playlists count.
     */
    public void incrementAnalyzedPlaylistsCount() {
        Map<String, Integer> stats = loadStats();
        int currentCount = stats.getOrDefault("analyzedPlaylistsCount", 0);
        stats.put("analyzedPlaylistsCount", currentCount + 1);
        saveStats(stats);
    }
}
