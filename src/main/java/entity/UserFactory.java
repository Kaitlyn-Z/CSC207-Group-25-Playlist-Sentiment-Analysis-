package entity;

import java.time.LocalDateTime;

public class UserFactory {
    public User create(String spotifyId,
                       String displayName,
                       String accessToken,
                       String refreshToken,
                       LocalDateTime tokenExpiry) {
        return new User(spotifyId, displayName, accessToken, refreshToken, tokenExpiry);
    }
}


