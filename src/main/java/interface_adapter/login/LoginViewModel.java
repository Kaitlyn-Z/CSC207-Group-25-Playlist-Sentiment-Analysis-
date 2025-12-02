package interface_adapter.login;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import interface_adapter.ViewModel;

public class LoginViewModel extends ViewModel{

    public static class State {
        public String error = "";
        public String displayName = "";
        public boolean loggedIn = false;
    }

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private State state = new State();
    public static final String VIEW_NAME = "loginView";

    public LoginViewModel() {
        super(VIEW_NAME);
    }

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
