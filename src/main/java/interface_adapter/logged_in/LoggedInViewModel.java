package interface_adapter.logged_in;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class LoggedInViewModel {

    public static class State {
        public String displayName = "";
        public String spotifyId = "";
    }

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private State state = new State();

    public State getState() {
        return state;
    }

    public void setDisplayName(String displayName) {
        state.displayName = displayName;
        support.firePropertyChange("state", null, state);
    }

    public void setSpotifyId(String spotifyId) {
        state.spotifyId = spotifyId;
        support.firePropertyChange("state", null, state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
