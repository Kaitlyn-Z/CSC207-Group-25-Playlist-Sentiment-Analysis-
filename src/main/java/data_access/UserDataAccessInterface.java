package data_access;

import entity.User;

/**
 * Shared data-access boundary for user operations.
 *
 * Implemented by DBUserDataAccessObject (and SpotifyUserDataAccessObject if you keep it).
 * Used by LoginInteractor and tests.
 */
public interface UserDataAccessInterface {

    // ========== Basic CRUD by Spotify ID ==========

    boolean existsBySpotifyId(String spotifyId);

    User getBySpotifyId(String spotifyId);

    void save(User user);

    // ========== Current User Session ==========

    void setCurrentUser(User user);

    User getCurrentUser();

    void clearCurrentUser();

    // ========== Spotify Login Integration Point ==========

    /**
     * Uses Spotify (and optionally the DB) to log in with an auth code / token.
     * Returns the up-to-date User entity.
     *
     * For now, your DBUserDataAccessObject implementation treats `code`
     * as an access token and calls Spotify's /me endpoint.
     */
    User createOrUpdateUserFromSpotifyCode(String code) throws Exception;
}
