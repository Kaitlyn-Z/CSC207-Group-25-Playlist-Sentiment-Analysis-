package view;

import interface_adapter.analysis.AnalysisController;
import interface_adapter.analysis.AnalysisState;
import interface_adapter.analysis.AnalysisViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The main view for viewing the Lyric Sentiment Analysis Summary.
 * It observes the AnalysisViewModel for state changes and updates the UI accordingly.
 */
public class AnalysisView extends JPanel implements ActionListener, PropertyChangeListener {

    public static final String VIEW_NAME = "analysis";

    // Components
    private final JTextArea lyricsArea = new JTextArea(10, 40);
    private final JButton analyzeButton;
    private final SentimentPanel sentimentPanel = new SentimentPanel();

    // Interface Adapter dependencies
    // Controller is non-final and set later by AppBuilder
    private AnalysisController analysisController;
    private final AnalysisViewModel analysisViewModel;

    public AnalysisView(AnalysisViewModel analysisViewModel) {
        // NOTE: The controller is NOT set here. It's set via the setter by AppBuilder later.
        this.analysisViewModel = analysisViewModel;
        this.analysisViewModel.addPropertyChangeListener(this);

        // Initialize the analyze button
        this.analyzeButton = new JButton(AnalysisViewModel.ANALYZE_BUTTON_LABEL);
        this.setLayout(new BorderLayout(10, 10));

        // --- 1. Input Panel ---
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Enter Song Lyrics"));

        // Default lyrics for testing
        String DEFAULT_LYRICS = "I walk the lonely road, the only one that I have ever known. Don't know where it goes, but it's home to me and I walk alone.";
        lyricsArea.setText(DEFAULT_LYRICS);

        inputPanel.add(new JScrollPane(lyricsArea), BorderLayout.CENTER);

        // Button setup
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(analyzeButton);
        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Attach action listener to this class instance
        analyzeButton.addActionListener(this);

        // --- 2. Visualization Panel ---
        // SentimentPanel is already initialized

        // --- Assemble View ---
        this.add(inputPanel, BorderLayout.NORTH);
        this.add(sentimentPanel, BorderLayout.CENTER);

        // Initial update
        updateViewFromState(analysisViewModel.getState());
    }

    /**
     * Setter required to inject the controller after the view is constructed.
     * @param analysisController The controller instance.
     */
    public void setAnalysisController(AnalysisController analysisController) {
        this.analysisController = analysisController;
    }

    /**
     * Handles button clicks.
     * @param e the ActionEvent to react to
     */
    //TODO: Now deriving lyrics and analysis are merged together, action performed should be moved to loggedin view,
    //TODO: I have written a button for analyzing and a method analyzeSentiments.addActionListener(which is a lambda type) there,
    //TODO: lambda type has combined addactionlistener with actionperformed together
    //TODO: So you can consider modifying codes of analyzeSentiments.addActionListener
    //TODO: But I feel what I wrote is the same thing as yours, just change some name
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(analyzeButton)) {
            // Check if the controller has been set by the AppBuilder
            if (this.analysisController != null) {
                String lyrics = lyricsArea.getText();
                // Delegate to the controller
                analysisController.execute(lyrics);
            } else {
                // Should not happen in a properly wired application, but good for robustness
                JOptionPane.showMessageDialog(this, "Application is not fully initialized. Analysis controller is missing.", "System Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Listener for changes in the AnalysisViewModel's state.
     * @param evt The property change event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        AnalysisState state = (AnalysisState) evt.getNewValue();
        updateViewFromState(state);
    }

    /**
     * Updates the UI components based on the current state of the ViewModel.
     * @param state The current analysis state.
     */
    private void updateViewFromState(AnalysisState state) {
        // Update button and loading state
        analyzeButton.setEnabled(!state.isLoading());
        if (state.isLoading()) {
            analyzeButton.setText("Analyzing...");
            sentimentPanel.setLoading(true);
        } else {
            analyzeButton.setText(AnalysisViewModel.ANALYZE_BUTTON_LABEL);
            sentimentPanel.setLoading(false);
        }

        // Update the visualization panel with the result
        sentimentPanel.setResult(state.getResult());

        // Handle errors
        if (state.getErrorMessage() != null && !state.isLoading()) {
            JOptionPane.showMessageDialog(this, state.getErrorMessage(), "Analysis Error", JOptionPane.ERROR_MESSAGE);
            // Clear the error message after showing it, so it doesn't reappear on subsequent events
            state.setErrorMessage(null);
        }
    }

    public String getViewName() {
        return VIEW_NAME;
    }
}