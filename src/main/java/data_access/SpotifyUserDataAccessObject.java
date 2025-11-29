package data_access;

import entity.User;

import java.time.LocalDateTime;

/**
 * Placeholder Spotify-based User DAO.
 *
 * This class is included so the project compiles cleanly and the
 * Spotify integration point is clear. It does NOT currently perform
 * real Spotify API calls — those will be added later.
 *
 * The structure matches Clean Architecture: LoginInteractor depends
 * only on UserDataAccessInterface, and real API logic would live here.
 */
public class SpotifyUserDataAccessObject implements UserDataAccessInterface {

    private User currentUser;

    public SpotifyUserDataAccessObject() {
        // Later: inject a real Spotify client here.
    }

    @Override
    public boolean existsBySpotifyId(String spotifyId) {
        return false;
    }

    @Override
    public User getBySpotifyId(String spotifyId) {
        return null;
    }

    @Override
    public void save(User user) {
        this.currentUser = user;
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

    @Override
    public User createOrUpdateUserFromSpotifyCode(String code) throws Exception {
        // 🔴 TEMP STUB: behaves similarly to DBUserDataAccessObject's stub,
        // just to satisfy the interface. You are NOT using this class yet.
        String spotifyId = code;
        String displayName = "User_" + code;
        String accessToken = "access_token_" + code;
        String refreshToken = "refresh_token_" + code;
        LocalDateTime expiry = LocalDateTime.now().plusHours(1);

        User user = new User(spotifyId, displayName, accessToken, refreshToken, expiry);
        save(user);
        setCurrentUser(user);
        return user;
    }
}
