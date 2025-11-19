package interface_adapter.login;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class LoginViewModel {

    public static class State {
        public String error = "";
        public String displayName = "";
        public boolean loggedIn = false;
    }

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private State state = new State();

    public State getState() { return state; }

    public void setError(String error) {
        state.error = error;
        support.firePropertyChange("state", null, state);
    }

    public void setLoggedIn(String displayName) {
        state.loggedIn = true;
        state.displayName = displayName;
        state.error = "";
        support.firePropertyChange("state", null, state);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
