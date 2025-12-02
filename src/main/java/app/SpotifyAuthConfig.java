package app;

public final class SpotifyAuthConfig {

    // Set these in your Run Configuration or OS env:
    // SPOTIFY_CLIENT_ID, SPOTIFY_CLIENT_SECRET
    public static final String CLIENT_ID = System.getenv("SPOTIFY_CLIENT_ID");
    public static final String CLIENT_SECRET = System.getenv("SPOTIFY_CLIENT_SECRET");

    // MUST MATCH the Redirect URI you added in Spotify Developer Dashboard
    // e.g. https://example.com/callback
    public static final String REDIRECT_URI = "https://example.com/callback";

    // Scopes you need. At minimum, read playlists.
    public static final String SCOPES =
            "user-read-email playlist-read-private playlist-read-collaborative";

    private SpotifyAuthConfig() {
        // utility class
    }

    public static void validate() {
        if (CLIENT_ID == null || CLIENT_ID.isBlank()) {
            throw new IllegalStateException("SPOTIFY_CLIENT_ID env var is not set.");
        }
        if (CLIENT_SECRET == null || CLIENT_SECRET.isBlank()) {
            throw new IllegalStateException("SPOTIFY_CLIENT_SECRET env var is not set.");
        }
    }
}
