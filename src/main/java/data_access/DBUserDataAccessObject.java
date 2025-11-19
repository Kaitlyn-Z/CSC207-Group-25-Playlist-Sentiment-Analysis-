package data_access;

import entity.User;
import entity.UserFactory;

public class DBUserDataAccessObject implements UserDataAccessInterface {

    // whatever fields/constructors already exist

    private final UserFactory userFactory;

    public DBUserDataAccessObject(UserFactory userFactory) { this.userFactory = userFactory; }

    @Override
    public boolean existsBySpotifyId(String spotifyId) {
        // implement using your DB logic
        // (or stub for now)
        return false;
    }

    @Override
    public User getBySpotifyId(String spotifyId) {
        // implement using your DB logic
        return null;
    }

    @Override
    public void save(User user) {
        // implement DB insert/update
    }

    private User currentUser;

    @Override
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void clearCurrentUser() {
        this.currentUser = null;
    }
}
