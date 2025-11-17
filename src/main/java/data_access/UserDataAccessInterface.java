package data_access;

import entity.User;

public interface UserDataAccessInterface {

    boolean existsBySpotifyId(String spotifyId);

    User getBySpotifyId(String spotifyId);

    void save(User user);

    void setCurrentUser(User user);

    User getCurrentUser();

    void clearCurrentUser();
}
