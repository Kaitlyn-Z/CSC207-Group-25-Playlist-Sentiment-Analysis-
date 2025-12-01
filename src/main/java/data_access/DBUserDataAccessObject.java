package data_access;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import entity.User;
import entity.UserFactory;
import use_case.login.LoginUserDataAccessInterface;
import use_case.logout.LogoutUserDataAccessInterface;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Concrete User DAO used by the app right now.
 *
 * It implements:
 * - LoginUserDataAccessInterface (for the login use case),
 * - LogoutUserDataAccessInterface (for logout use case),
 * - UserDataAccessInterface (shared data-access boundary).
 *
 * Internally it uses an in-memory Map for users so the app can run
 * without a real DB. Spotify is really called in createOrUpdateUserFromSpotifyCode.
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
     * For this project, we treat the given value as a *Spotify access token*.
     * We call Spotify's /me endpoint to get the user's profile and either
     * create or update a User entity in our in-memory store.
     */
    @Override
    public User createOrUpdateUserFromSpotifyCode(String code) throws Exception {
        // 1) Build HTTP client & request to Spotify's /me endpoint
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/me"))
                .header("Authorization", "Bearer " + code)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            // If the token is invalid or expired, Spotify returns 401 or similar
            throw new RuntimeException(
                    "Spotify /me call failed with status " + response.statusCode() +
                            " body: " + response.body()
            );
        }

        // 2) Parse JSON: we need at least `id` and `display_name`
        Gson gson = new Gson();
        Map<String, Object> body =
                gson.fromJson(response.body(), new TypeToken<Map<String, Object>>() {}.getType());

        String spotifyId = (String) body.get("id");
        String displayName = (String) body.get("display_name");
        if (displayName == null || displayName.isBlank()) {
            displayName = "Spotify User " + spotifyId;
        }

        // 3) Create or update a User entity
        User user;
        if (existsBySpotifyId(spotifyId)) {
            // Existing user in our in-memory "DB"
            user = getBySpotifyId(spotifyId);
            // (Optionally update tokens here if needed)
        } else {
            // New user: use the factory so other code doesn't depend on User's constructor
            String accessToken = code;   // we store the raw token for now
            String refreshToken = null;  // not implemented in this mini flow
            LocalDateTime expiry = LocalDateTime.now().plusHours(1); // fake expiry

            user = userFactory.create(spotifyId, displayName, accessToken, refreshToken, expiry);
        }

        // 4) Save + set current user
        save(user);
        setCurrentUser(user);

        return user;
    }
}
