package use_case.analyze_playlist;

import java.util.Map;

public interface AnalysisStatsDataAccessInterface {

    Map<String, Integer> loadStats();

    void saveStats(Map<String, Integer> stats);

    int getAnalyzedPlaylistsCount();

    void incrementAnalyzedPlaylistsCount();

}
