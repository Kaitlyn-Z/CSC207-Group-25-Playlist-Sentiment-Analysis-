package interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ViewManagerModel {

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private String state;  // current active view name

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    // 🔹 AppBuilder: viewManagerModel.firePropertyChange();
    public void firePropertyChange() {
        support.firePropertyChange("view", null, this.state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
