package data_access;

import entity.User;
import entity.UserFactory;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;

public class DBUserDataAccessObject
        implements LoginUserDataAccessInterface, LogoutUserDataAccessInterface, UserDataAccessInterface{

    /*
    necessity & variable according to the needs of API
    private static final int SUCCESS_CODE = 200;
    private static final String STATUS_CODE_LABEL = "status_code";
    private static final String CONTENT_TYPE_LABEL = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json";

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String MESSAGE = "message";
    */

    private final UserFactory userFactory;

    // --- keep track of the currently logged-in user in memory ---
    private User currentUser;

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
