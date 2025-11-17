package interface_adapter.analysis;

import use_case.analyze_playlist.AnalyzePlaylistOutputBoundary;
import use_case.analyze_playlist.AnalyzePlaylistOutputData;

import javax.swing.*;

/**
 * The Presenter implementation. It receives data from the Interactor and updates the View Model.
 */
public class AnalysisPresenter implements AnalyzePlaylistOutputBoundary {

    private final AnalysisViewModel analysisViewModel;

    public AnalysisPresenter(AnalysisViewModel analysisViewModel) {
        this.analysisViewModel = analysisViewModel;
    }

    /**
     * Called by the Interactor on successful completion.
     * Updates the ViewModel with the sentiment result and stops loading.
     * @param outputData The output data containing the SentimentResult.
     */
    @Override
    public void prepareSuccessView(AnalyzePlaylistOutputData outputData) {
        // Update the state on the AWT Event Dispatch Thread (Swing requirement)
        SwingUtilities.invokeLater(() -> {
            AnalysisState state = analysisViewModel.getState();
            state.setLoading(false);
            state.setResult(outputData.getResult());
            analysisViewModel.firePropertyChanged();
        });
    }

    /**
     * Called by the Interactor on failure.
     * Updates the ViewModel with the error message and stops loading.
     * @param error A string describing the error.
     */
    @Override
    public void prepareFailView(String error) {
        // Update the state on the AWT Event Dispatch Thread (Swing requirement)
        SwingUtilities.invokeLater(() -> {
            AnalysisState state = analysisViewModel.getState();
            state.setLoading(false);
            state.setErrorMessage(error);
            analysisViewModel.firePropertyChanged();

            // Optionally show a dialog box for the error
            JOptionPane.showMessageDialog(null, error, "Analysis Error", JOptionPane.ERROR_MESSAGE);
        });
    }
}