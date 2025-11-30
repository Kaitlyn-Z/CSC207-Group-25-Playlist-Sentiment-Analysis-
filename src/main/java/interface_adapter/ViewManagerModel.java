package interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ViewManagerModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private String state;
    // current active view name

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * Notifies listeners (like ViewManager) that the active view name has changed.
     * ViewManager listens for property name "state", so we must use the same label here.
     */
    public void firePropertyChange() {
        support.firePropertyChange("state", null, this.state);
    }

    /**
     * Add property change listener.
     * @param listener PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
