package use_case.login;

public class LoginInputData {
    private final String spotifyIdOrCode;

    public LoginInputData(String spotifyIdOrCode) {
        this.spotifyIdOrCode = spotifyIdOrCode;
    }

    public String getSpotifyIdOrCode() {
        return spotifyIdOrCode;
    }
}
