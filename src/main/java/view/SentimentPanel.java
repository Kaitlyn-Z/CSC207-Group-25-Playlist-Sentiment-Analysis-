package view;

import entity.SentimentResult; // <-- Crucial Import
import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * A custom Swing panel responsible for displaying the structured SentimentResult
 * returned by the Gemini API analysis.
 */
public class SentimentPanel extends JPanel {

    private final JLabel overallCategoryLabel;
    private final JProgressBar scoreBar;
    private final JTextArea summaryArea;
    private final JLabel breakdownLabel;
    private final JProgressBar loadingBar;

    public SentimentPanel() {
        // Set up the panel layout
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createTitledBorder("Analysis Summary"));
        this.setBackground(new Color(245, 245, 245));

        // 1. Loading Indicator
        loadingBar = new JProgressBar();
        loadingBar.setIndeterminate(true);
        loadingBar.setStringPainted(true);
        loadingBar.setString("Waiting for analysis...");
        loadingBar.setVisible(false); // Hidden by default
        this.add(loadingBar);
        this.add(Box.createVerticalStrut(10));

        // 2. Overall Category
        overallCategoryLabel = new JLabel("Overall Sentiment: N/A");
        overallCategoryLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        this.add(overallCategoryLabel);
        this.add(Box.createVerticalStrut(10));

        // 3. Numerical Score Bar
        JLabel scoreLabel = new JLabel("Sentiment Score (-1.0 to 1.0):");
        scoreBar = new JProgressBar(-100, 100); // Scale internally to -100 to 100
        scoreBar.setStringPainted(true);
        scoreBar.setForeground(new Color(60, 179, 113)); // Medium Sea Green
        scoreBar.setBackground(new Color(255, 99, 71)); // Tomato Red
        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.add(scoreLabel, BorderLayout.NORTH);
        scorePanel.add(scoreBar, BorderLayout.CENTER);
        this.add(scorePanel);
        this.add(Box.createVerticalStrut(15));

        // 4. Summary Text
        JLabel summaryTitle = new JLabel("Detailed LLM Summary:");
        summaryTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        summaryArea = new JTextArea(5, 40);
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setWrapStyleWord(true);
        summaryArea.setText("The analysis results will appear here.");
        JScrollPane scrollPane = new JScrollPane(summaryArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        this.add(summaryTitle);
        this.add(scrollPane);
        this.add(Box.createVerticalStrut(15));

        // 5. Breakdown Label (Placeholder for detailed breakdown)
        breakdownLabel = new JLabel("Sentiment Breakdown: ");
        this.add(breakdownLabel);
    }

    /**
     * Updates the panel when a new SentimentResult is available.
     * This method resolves the error you encountered because it uses entity.SentimentResult.
     * @param result The SentimentResult entity to display.
     */
    public void setResult(SentimentResult result) {
        if (result == null) {
            overallCategoryLabel.setText("Overall Sentiment: N/A");
            scoreBar.setValue(0);
            scoreBar.setString("0.00");
            summaryArea.setText("The analysis results will appear here.");
            breakdownLabel.setText("Sentiment Breakdown: ");
            return;
        }

        overallCategoryLabel.setText("Overall Sentiment: " + result.getOverallCategory());

        // Update score bar: Map numericalScore (-1.0 to 1.0) to bar range (-100 to 100)
        int scoreValue = (int) (result.getNumericalScore() * 100);
        scoreBar.setValue(scoreValue);
        scoreBar.setString(String.format("%.2f", result.getNumericalScore()));

        // Set color dynamically (e.g., green for positive, red for negative)
        if (result.getNumericalScore() > 0.1) {
            scoreBar.setForeground(new Color(60, 179, 113)); // Positive Green
        } else if (result.getNumericalScore() < -0.1) {
            scoreBar.setForeground(new Color(255, 99, 71)); // Negative Red
        } else {
            scoreBar.setForeground(new Color(173, 216, 230)); // Neutral Blue
        }

        summaryArea.setText(result.getSummaryText());
        summaryArea.setCaretPosition(0); // Scroll to top

        // Display the breakdown
        StringBuilder breakdown = new StringBuilder("Sentiment Breakdown: ");
        if (!result.getSentimentBreakdown().isEmpty()) {
            for (Map.Entry<String, Double> entry : result.getSentimentBreakdown().entrySet()) {
                breakdown.append(String.format("%s: %.0f%% | ",
                        entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1),
                        entry.getValue() * 100));
            }
            breakdownLabel.setText(breakdown.substring(0, breakdown.length() - 3)); // Remove trailing ' | '
        } else {
            breakdownLabel.setText("Sentiment Breakdown: Detailed breakdown not provided.");
        }
    }

    /**
     * Sets the loading state of the panel, showing/hiding the progress bar.
     * @param isLoading True to show loading state, false to hide.
     */
    public void setLoading(boolean isLoading) {
        loadingBar.setVisible(isLoading);
        // Hide other elements when loading
        overallCategoryLabel.setVisible(!isLoading);
        scoreBar.getParent().setVisible(!isLoading);
        summaryArea.getParent().setVisible(!isLoading);
        breakdownLabel.setVisible(!isLoading);
    }
}