package interface_adapter.analysis;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The Analysis View Model holds the state and notifies listeners (the view) of changes.
 */
public class AnalysisViewModel {
    public static final String TITLE_LABEL = "Lyric Sentiment Analysis";
    public static final String ANALYZE_BUTTON_LABEL = "Analyze Sentiment";

    private AnalysisState state = new AnalysisState();

    public void setState(AnalysisState state) {
        this.state = state;
    }

    public AnalysisState getState() {
        return state;
    }

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    // This method is called by the Presenter
    public void firePropertyChanged() {
        support.firePropertyChange("state", null, this.state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}