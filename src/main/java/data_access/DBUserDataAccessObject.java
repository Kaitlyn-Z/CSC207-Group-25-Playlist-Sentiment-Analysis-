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
 * - LogoutUserDataAccessInterface (for logout use case).
 *
 * Internally it uses an in-memory Map for users so the app can run
 * without a real DB. Spotify is really called in createOrUpdateUserFromSpotifyCode.
 */
public class DBUserDataAccessObject implements
        LoginUserDataAccessInterface,
        LogoutUserDataAccessInterface {

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
    // ================== Spotify Login Integration Point ==================

    /**
     * Full Spotify OAuth implementation:
     * - Treats the given value as an authorization code.
     * - Exchanges it for access/refresh tokens at /api/token.
     * - Calls /v1/me to get the user's Spotify profile.
     * - Creates/updates a User via UserFactory.
     */
    @Override
    public User createOrUpdateUserFromSpotifyCode(String code) throws Exception {
        app.SpotifyAuthConfig.validate(); // ensure env vars exist

        final HttpClient httpClient = HttpClient.newHttpClient();
        final Gson gson = new Gson();

        // ===== 1) Exchange authorization code for tokens at /api/token =====
        String form = "grant_type=authorization_code"
                + "&code=" + java.net.URLEncoder.encode(code, java.nio.charset.StandardCharsets.UTF_8)
                + "&redirect_uri=" + java.net.URLEncoder.encode(
                app.SpotifyAuthConfig.REDIRECT_URI,
                java.nio.charset.StandardCharsets.UTF_8
        )
                + "&client_id=" + java.net.URLEncoder.encode(
                app.SpotifyAuthConfig.CLIENT_ID,
                java.nio.charset.StandardCharsets.UTF_8
        )
                + "&client_secret=" + java.net.URLEncoder.encode(
                app.SpotifyAuthConfig.CLIENT_SECRET,
                java.nio.charset.StandardCharsets.UTF_8
        );

        HttpRequest tokenRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> tokenResponse =
                httpClient.send(tokenRequest, HttpResponse.BodyHandlers.ofString());

        if (tokenResponse.statusCode() != 200) {
            throw new RuntimeException(
                    "Spotify token endpoint failed with status "
                            + tokenResponse.statusCode() + " body: " + tokenResponse.body()
            );
        }

        Map<String, Object> tokenJson =
                gson.fromJson(tokenResponse.body(), new TypeToken<Map<String, Object>>() {
                }.getType());

        String accessToken = (String) tokenJson.get("access_token");
        String refreshToken = (String) tokenJson.get("refresh_token");
        Number expiresIn = (Number) tokenJson.get("expires_in"); // seconds

        if (accessToken == null || accessToken.isBlank()) {
            throw new RuntimeException("Spotify token response missing access_token.");
        }

        LocalDateTime expiry = null;
        if (expiresIn != null) {
            long secs = expiresIn.longValue();
            expiry = LocalDateTime.now().plusSeconds(secs);
        }

        // ===== 2) Call /v1/me with access token =====
        HttpRequest meRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/me"))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> meResponse =
                httpClient.send(meRequest, HttpResponse.BodyHandlers.ofString());

        if (meResponse.statusCode() != 200) {
            throw new RuntimeException(
                    "Spotify /me call failed with status "
                            + meResponse.statusCode() + " body: " + meResponse.body()
            );
        }

        Map<String, Object> profileJson =
                gson.fromJson(meResponse.body(), new TypeToken<Map<String, Object>>() {
                }.getType());

        String spotifyId = (String) profileJson.get("id");
        String displayName = (String) profileJson.get("display_name");

        if (spotifyId == null || spotifyId.isBlank()) {
            throw new RuntimeException("Spotify profile missing id: " + meResponse.body());
        }
        if (displayName == null || displayName.isBlank()) {
            displayName = "Spotify User " + spotifyId;
        }

        // ===== 3) Create or update User in our in-memory store =====
        User user;
        if (existsBySpotifyId(spotifyId)) {
            user = getBySpotifyId(spotifyId);
            // Optional: update tokens on existing user if you want.
            // For simplicity, we just create a fresh User via factory below.
            user = userFactory.create(spotifyId, displayName, accessToken, refreshToken, expiry);
        } else {
            user = userFactory.create(spotifyId, displayName, accessToken, refreshToken, expiry);
        }

        save(user);
        setCurrentUser(user);

        return user;
    }
}
