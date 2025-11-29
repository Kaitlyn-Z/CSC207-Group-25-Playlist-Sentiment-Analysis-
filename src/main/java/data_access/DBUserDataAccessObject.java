package data_access;

import entity.User;
import entity.UserFactory;
import use_case.logout.LogoutUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete User DAO used by the app right now.
 *
 * It implements:
 * - LoginUserDataAccessInterface (for legacy use-case interfaces),
 * - LogoutUserDataAccessInterface (for logout use case),
 * - UserDataAccessInterface (the main CA boundary for login).
 *
 * Internally it uses an in-memory Map for users so the app can run
 * without a real DB. You can later replace this with real persistence.
 */
public class DBUserDataAccessObject implements
        LoginUserDataAccessInterface,
        LogoutUserDataAccessInterface,
        UserDataAccessInterface {

    private final UserFactory userFactory;

    // Simple in-memory store: spotifyId -> User
    private final Map<String, User> usersBySpotifyId = new HashMap<>();

    // Currently logged-in user
    private User currentUser;

    public DBUserDataAccessObject(UserFactory userFactory) {
        this.userFactory = userFactory;
    }

    // ================== Basic CRUD by Spotify ID ==================

    @Override
    public boolean existsBySpotifyId(String spotifyId) {
        return usersBySpotifyId.containsKey(spotifyId);
    }

    @Override
    public User getBySpotifyId(String spotifyId) {
        return usersBySpotifyId.get(spotifyId);
    }

    @Override
    public void save(User user) {
        usersBySpotifyId.put(user.getSpotifyId(), user);
    }

    // ================== Current User Session ==================

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

    // ================== Spotify Login Integration Point ==================

    /**
     * For now, this is a stub that *simulates* Spotify login:
     * it treats the code as the spotifyId and fabricates tokens.
     *
     * Later, you replace the internals with calls to the real Spotify helper
     * from the project blueprint (exchange code, get profile, etc.).
     */
    @Override
    public User createOrUpdateUserFromSpotifyCode(String code) throws Exception {
        // 🔴 TEMP BEHAVIOUR: treat `code` as the spotifyId
        final String spotifyId = code;
        final String displayName = "User_" + code;
        final String accessToken = "access_token_" + code;
        final String refreshToken = "refresh_token_" + code;
        final LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        User user;
        if (existsBySpotifyId(spotifyId)) {
            // Existing user — you could also update tokens here if you want
            user = getBySpotifyId(spotifyId);
        } else {
            // Create a new user entity using the factory
            user = userFactory.create(spotifyId, displayName, accessToken, refreshToken, expiry);
        }

        save(user);
        setCurrentUser(user);

        return user;
    }
}
