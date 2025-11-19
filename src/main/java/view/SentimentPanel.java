package view;

import entity.SentimentResult;
import javax.swing.*;
import java.awt.*;

/**
 * A custom Swing panel responsible for displaying the structured Sentiment Analysis Result
 * returned by the Gemini API analysis.
 */
public class SentimentPanel extends JPanel {

    private final JLabel sentimentWordLabel;
    private final JTextArea sentimentExplanationArea;
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
        loadingBar.setString("Waiting for Sentiment analysis...");
        loadingBar.setVisible(false); // Hidden by default
        this.add(loadingBar);
        this.add(Box.createVerticalStrut(10));

        // 2. Sentiment Word
        sentimentWordLabel = new JLabel("Sentiment Word: N/A");
        sentimentWordLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        sentimentWordLabel.setForeground(new Color(30, 144, 255)); // Dodger Blue for emphasis
        this.add(sentimentWordLabel);
        this.add(Box.createVerticalStrut(10));

        // 3. Sentiment Explanation (Detailed Explanation)
        JLabel blurbTitle = new JLabel("Sentiment Explanation:");
        blurbTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        sentimentExplanationArea = new JTextArea(8, 40);
        sentimentExplanationArea.setEditable(false);
        sentimentExplanationArea.setLineWrap(true);
        sentimentExplanationArea.setWrapStyleWord(true);
        sentimentExplanationArea.setText("The descriptive sentiment analysis of the lyrics' feeling will appear here.");
        JScrollPane scrollPane = new JScrollPane(sentimentExplanationArea);

        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        this.add(blurbTitle);
        this.add(scrollPane);
        this.add(Box.createVerticalStrut(15));
    }

    /**
     * Updates the panel when a new SentimentResult (Sentiment Analysis) is available.
     * @param result The SentimentResult entity to display.
     */
    public void setResult(SentimentResult result) {
        if (result == null) {
            sentimentWordLabel.setText("Sentiment Word: N/A");
            sentimentExplanationArea.setText("The descriptive sentiment analysis of the lyrics' feeling will appear here.");
            return;
        }

        // Display the new Sentiment fields (using the updated getters from the Entity)
        sentimentWordLabel.setText("Sentiment Word: " + result.getSentimentWord());
        sentimentExplanationArea.setText(result.getSentimentExplanation());
        sentimentExplanationArea.setCaretPosition(0); // Scroll to top
    }

    /**
     * Sets the loading state of the panel, showing/hiding the progress bar.
     * @param isLoading True to show loading state, false to hide.
     */
    public void setLoading(boolean isLoading) {
        loadingBar.setVisible(isLoading);

        // Hide other elements when loading
        sentimentWordLabel.setVisible(!isLoading);
        sentimentExplanationArea.getParent().setVisible(!isLoading); // Parent is the JScrollPane
    }
}