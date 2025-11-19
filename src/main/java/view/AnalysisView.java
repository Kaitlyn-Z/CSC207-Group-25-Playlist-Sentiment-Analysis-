package view;

import data_access.DBGeminiDataAccessObject;
import interface_adapter.analysis.AnalysisController;
import interface_adapter.analysis.AnalysisState;
import interface_adapter.analysis.AnalysisViewModel;
import interface_adapter.analysis.AnalysisPresenter;
import use_case.analyze_playlist.AnalyzePlaylistInputBoundary;
import use_case.analyze_playlist.AnalyzePlaylistInteractor;
import use_case.analyze_playlist.AnalyzePlaylistOutputBoundary;
import use_case.analyze_playlist.SentimentDataAccessInterface; // <-- NEW IMPORT

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The main view for viewing the Lyric Sentiment Analysis Summary.
 * It observes the AnalysisViewModel for state changes and updates the UI accordingly.
 */
public class AnalysisView extends JPanel implements PropertyChangeListener {

    public final String viewName = "analysis";

    // Components
    private final JTextArea lyricsArea = new JTextArea(10, 40);
    private final JButton analyzeButton;
    private final SentimentPanel sentimentPanel = new SentimentPanel();

    // Interface Adapter dependencies
    private final AnalysisController analysisController;
    private final AnalysisViewModel analysisViewModel;

    public AnalysisView(AnalysisController analysisController, AnalysisViewModel analysisViewModel) {
        this.analysisController = analysisController;
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

        // Attach action listener
        analyzeButton.addActionListener(e -> {
            if (e.getSource().equals(analyzeButton)) {
                String lyrics = lyricsArea.getText();
                // Delegate to the controller
                analysisController.execute(lyrics);
            }
        });

        // --- 2. Visualization Panel ---
        // SentimentPanel is already initialized

        // --- Assemble View ---
        this.add(inputPanel, BorderLayout.NORTH);
        this.add(sentimentPanel, BorderLayout.CENTER);

        // Initial update
        updateViewFromState(analysisViewModel.getState());
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

    // Simple main method for testing this component, assuming all dependencies are available.
    public static void main(String[] args) {
        // --- Setup Dependencies (Simulated for Demo) ---
        // 1. Data Access (The Gemini API call)
        // NOTE: Ensure your API Key is set in DBGeminiDataAccessObject.java
        SentimentDataAccessInterface dao = new DBGeminiDataAccessObject(); // <-- NOW IT WORKS

        // 2. View Model
        AnalysisViewModel viewModel = new AnalysisViewModel();

        // 3. Presenter
        AnalyzePlaylistOutputBoundary presenter = new AnalysisPresenter(viewModel);

        // 4. Interactor
        AnalyzePlaylistInputBoundary interactor = new AnalyzePlaylistInteractor(dao, presenter);

        // 5. Controller
        AnalysisController controller = new AnalysisController(interactor, viewModel);

        // --- Build and Run UI ---
        SwingUtilities.invokeLater(() -> {
            JFrame application = new JFrame(AnalysisViewModel.TITLE_LABEL);
            application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            AnalysisView analysisView = new AnalysisView(controller, viewModel);

            application.add(analysisView);
            application.pack();
            application.setLocationRelativeTo(null);
            application.setVisible(true);
        });
    }
}