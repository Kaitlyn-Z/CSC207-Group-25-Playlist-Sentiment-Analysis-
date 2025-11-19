package use_case.login;

public class LoginOutputData {
    private final String displayName;
    private final String spotifyId;

    public LoginOutputData(String displayName, String spotifyId) {
        this.displayName = displayName;
        this.spotifyId = spotifyId;
    }

    public String getDisplayName() { return displayName; }
    public String getSpotifyId() { return spotifyId; }
}

