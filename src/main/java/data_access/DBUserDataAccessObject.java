package data_access;

import entity.User;
import entity.UserFactory;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;

public class DBUserDataAccessObject
        implements LoginUserDataAccessInterface, LogoutUserDataAccessInterface {

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

    public DBUserDataAccessObject(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    // ================== LOGIN-RELATED METHODS ==================
    // These will come from LoginUserDataAccessInterface (not implemented yet).
    // Example of something you might eventually have:
    //
    // @Override
    // public void saveLoggedInUser(User user) {
    //     this.currentUser = user;
    // }
    //
    // @Override
    // public User getCurrentUser() {
    //     return currentUser;
    // }

    // ================== LOGOUT-RELATED METHODS ==================

    /**
     * Clear the currently logged-in user.
     * This method is required by LogoutUserDataAccessInterface and is used
     * by the LogoutInteractor.
     */
    @Override
    public void clearCurrentUser() {
        this.currentUser = null;
        // If later you store session/refresh tokens, you can clear them here too.
    }

    //all concrete methods from use case data access interface corresponding to user, like: login,
    //determine if the user exists or not...
    //Based on how Spotify API works
    /*Example:
    @Override
    public boolean existsByName(String username) {
    } if login user data access interface has an abstract method called existsByName
     */
}
