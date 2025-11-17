package view;

import use_case.analyze_playlist.SentimentResult;

import javax.swing.*;
import java.awt.*;

/**
 * Custom Swing component to visualize the sentiment result as a bar.
 * Uses color and a bar to represent the score (-1.0 to 1.0).
 */
public class SentimentPanel extends JComponent {
    private SentimentResult result = null;
    private boolean loading = false;

    public SentimentPanel() {
        setPreferredSize(new Dimension(500, 200));
        setBorder(BorderFactory.createTitledBorder("Sentiment Visualization"));
    }

    /**
     * Sets the result data to be visualized and triggers a repaint.
     * @param result The sentiment data from the analysis.
     */
    public void setResult(SentimentResult result) {
        this.result = result;
        repaint();
    }

    /**
     * Toggles the loading state, displaying a loading message when true.
     * @param loading true if analysis is in progress.
     */
    public void setLoading(boolean loading) {
        this.loading = loading;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // 1. Loading State
        if (loading) {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
            String msg = "... Analyzing Lyrics ...";
            g2d.drawString(msg, width / 2 - g2d.getFontMetrics().stringWidth(msg) / 2, height / 2);
            return;
        }

        // 2. Initial/No Data State
        if (result == null) {
            g2d.setColor(Color.DARK_GRAY);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
            String msg = "Sentiment data will appear here.";
            g2d.drawString(msg, width / 2 - g2d.getFontMetrics().stringWidth(msg) / 2, height / 2);
            return;
        }

        double score = result.score();

        // 3. Draw the neutral line (0.0)
        int centerLine = width / 2;
        g2d.setColor(Color.GRAY);
        g2d.drawLine(centerLine, 20, centerLine, height - 70);

        // 4. Calculate bar width and color
        int maxBarWidth = centerLine - 50;
        int barWidth = (int) (maxBarWidth * Math.abs(score));

        // Determine color: Green for positive, Red for negative, Yellow for neutral (close to 0)
        Color barColor;
        if (score > 0.1) {
            float ratio = (float) (score - 0.1) / 0.9f;
            barColor = interpolateColor(Color.YELLOW, Color.GREEN, ratio);
        } else if (score < -0.1) {
            float ratio = (float) (Math.abs(score) - 0.1) / 0.9f;
            barColor = interpolateColor(Color.YELLOW, Color.RED, ratio);
        } else {
            barColor = Color.YELLOW; // Near neutral
        }

        // 5. Draw the sentiment bar
        g2d.setColor(barColor);
        int barHeight = height - 90;
        int barY = 30;

        if (score > 0) {
            g2d.fillRect(centerLine, barY, barWidth, barHeight);
        } else if (score < 0) {
            g2d.fillRect(centerLine - barWidth, barY, barWidth, barHeight);
        }

        // 6. Draw labels and score text
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2d.drawString("-1.0 (Negative)", 5, height / 2);
        g2d.drawString("1.0 (Positive)", width - 80, height / 2);

        // Draw the current score text
        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
        String scoreText = String.format("Score: %.2f (%s)", score, result.category());
        g2d.drawString(scoreText, centerLine - g2d.getFontMetrics().stringWidth(scoreText) / 2, height - 50);

        // Draw the explanation
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        String explanation = result.explanation();
        g2d.drawString("Summary: " + explanation, 20, height - 10);
    }

    /** Helper to interpolate between two colors. */
    private Color interpolateColor(Color c1, Color c2, float ratio) {
        float r = c1.getRed() * (1 - ratio) + c2.getRed() * ratio;
        float g = c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio;
        float b = c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio;
        return new Color((int) r / 255.0f, (int) g / 255.0f, (int) b / 255.0f);
    }
}