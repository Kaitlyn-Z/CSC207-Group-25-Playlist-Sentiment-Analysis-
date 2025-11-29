package data_access;

import entity.User;

public interface UserDataAccessInterface {

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
     * For now this can be a stub that just creates a fake user, but later
     * you will replace the internals with real Spotify API logic.
     */
    User createOrUpdateUserFromSpotifyCode(String code) throws Exception;
}
