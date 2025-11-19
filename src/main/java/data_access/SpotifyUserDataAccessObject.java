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
 * only on UserDataAccessInterface, and real API logic will live here.
 */
public class SpotifyUserDataAccessObject implements UserDataAccessInterface {

    private User currentUser;

    public SpotifyUserDataAccessObject() {
        // Nothing yet — no real Spotify login implemented.
        // Add fields here once you have an OAuth client, etc.
    }

    /**
     * For now, always return false. When Spotify login is implemented,
     * this will check a real database or token store.
     */
    @Override
    public boolean existsBySpotifyId(String spotifyId) {
        return false;
    }

    /**
     * For now, always return null. Once real Spotify calls exist,
     * this should lookup user info from DB or memory.
     */
    @Override
    public User getBySpotifyId(String spotifyId) {
        return null;
    }

    /**
     * Saves user created either from mock logic or future Spotify API.
     */
    @Override
    public void save(User user) {
        // In a real implementation, write to DB or persistence layer.
        this.currentUser = user;
    }

    /**
     * Sets the current logged-in user.
     */
    @Override
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Returns the current logged-in user, if any.
     */
    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Clears the current user session.
     */
    @Override
    public void clearCurrentUser() {
        this.currentUser = null;
    }


    // ===============================================================
    //        FUTURE REAL SPOTIFY INTEGRATION GOES HERE
    // ===============================================================
    //
    // Example of what will eventually be implemented:
    //
    // public User createUserFromSpotifyAuthCode(String code) {
    //      SpotifyTokenResponse tokens = spotifyClient.exchangeCode(code);
    //      SpotifyProfileResponse profile = spotifyClient.getCurrentUser(tokens.accessToken());
    //
    //      User user = new User(
    //          profile.id(),
    //          profile.displayName(),
    //          tokens.accessToken(),
    //          tokens.refreshToken(),
    //          LocalDateTime.now().plusSeconds(tokens.expiresIn())
    //      );
    //
    //      save(user);
    //      setCurrentUser(user);
    //
    //      return user;
    // }
    //
    // Until you build a real Spotify client class,
    // keeping this commented-out section prevents compile errors.
}
