package interface_adapter.logged_in;

import entity.Playlist;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.List;

public class LoggedInViewModel {

    public static class State {
        public String displayName = "";
        public String spotifyId = "";

        // new: selected playlist
        public Playlist selectedPlaylist = null;

        // new: bottom STATUS LABEL info
        public String statusMessage = "No playlist selected.";

        // NEW: playlists fetched from Spotify for this user
        // These are entity.Playlist objects with id, name, and songs JsonArray.
        public List<Playlist> playlists = Collections.emptyList();
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


    /**
     * NEW: store the playlists for the currently logged-in user.
     * This is temporary (in-memory) state, not written to the DB.
     */
    public void setPlaylists(List<Playlist> playlists) {
        state.playlists = playlists;
        support.firePropertyChange("state", null, state);
    }

    /**
     * Convenience getter if you need just the playlists.
     */
    public List<Playlist> getPlaylists() {
        return state.playlists;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    // new
    public void setSelectedPlaylist(Playlist playlist) {
        state.selectedPlaylist = playlist;

        if (playlist != null) {
            state.statusMessage = "Selected playlist: " + playlist.getPlaylistName();
        }
        else {
            state.statusMessage = "No playlist selected.";
        }

        support.firePropertyChange("state", null, state);
    }

    /**
     * Set Status Message.
     * @param message message
     */
    public void setStatusMessage(String message) {
        state.statusMessage = message;
        support.firePropertyChange("state", null, state);
    }


}
