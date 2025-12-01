package use_case.login;

import entity.User;

/**
 * Login data access boundary for Clean Architecture.
 *
 * The LoginInteractor depends on THIS interface, not on any concrete DB class.
 * The implementation (DBUserDataAccessObject) will handle real Spotify + DB logic.
 */
public interface LoginUserDataAccessInterface {

    boolean existsBySpotifyId(String spotifyId);

    User getBySpotifyId(String spotifyId);

    void save(User user);

    void setCurrentUser(User user);

    User getCurrentUser();

    void clearCurrentUser();

    /**
     * Uses Spotify (and optionally the DB) to log in with an auth code / token.
     * Returns the up-to-date User entity.
     *
     * In production, this will hit Spotify + DB.
     * In tests, we fake it.
     */
    User createOrUpdateUserFromSpotifyCode(String code) throws Exception;
}
