package data_access;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entity.Playlist;
import entity.PlaylistFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access class to fetch the current user's playlists from Spotify,
 * using a manually supplied token (copied from the Spotify Web API console).
 *
 * It returns Playlist entities with:
 *   - playlistId
 *   - playlistName
 *   - songs: JsonArray of {"artist": "...", "title": "..."}
 *
 * This class does NOT do OAuth or token refreshing.
 */
public class SpotifyUserPlaylistsDataAccessObject {

    private static final String BASE_URL = "https://api.spotify.com/v1";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final PlaylistFactory playlistFactory;

    public SpotifyUserPlaylistsDataAccessObject(PlaylistFactory playlistFactory) {
        this.playlistFactory = playlistFactory;
    }

    /**
     * Fetch all playlists for the user represented by the manually supplied token.
     *
     * @param spotifyUserToken a manually generated Spotify token (the long string after "Bearer ").
     * @return list of Playlist entities (id, name, songs).
     */
    public List<Playlist> getCurrentUserPlaylists(String spotifyUserToken)
            throws IOException, InterruptedException {

        // Call /me/playlists to get the user's playlists
        JsonObject root = sendGet("/me/playlists?limit=50", spotifyUserToken);

        JsonArray items = root.getAsJsonArray("items");
        List<Playlist> playlists = new ArrayList<>();

        if (items == null) {
            return playlists;
        }

        for (JsonElement element : items) {
            JsonObject playlistObj = element.getAsJsonObject();

            // Basic playlist fields
            String playlistId = playlistObj.get("id").getAsString();
            String playlistName = playlistObj.get("name").getAsString();

            // Get songs (title + artist) for this playlist
            JsonArray songs = fetchTracksForPlaylist(playlistId, spotifyUserToken);

            // Use your existing PlaylistFactory
            Playlist playlist = playlistFactory.create(playlistId, playlistName, songs);
            playlists.add(playlist);
        }

        return playlists;
    }

    /**
     * Helper: send a GET request to a Spotify Web API endpoint and parse the JSON response.
     */
    private JsonObject sendGet(String endpoint, String spotifyUserToken)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", "Bearer " + spotifyUserToken)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException(
                    "Spotify API error " + response.statusCode() + ": " + response.body()
            );
        }

        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    /**
     * Fetch tracks for a playlist and return them in the format expected by your Playlist entity:
     *   [{"artist": "...", "title": "..."}, ...]
     */
    private JsonArray fetchTracksForPlaylist(String playlistId, String spotifyUserToken)
            throws IOException, InterruptedException {

        JsonObject root = sendGet("/playlists/" + playlistId + "/tracks?limit=100", spotifyUserToken);

        JsonArray items = root.getAsJsonArray("items");
        JsonArray songs = new JsonArray();

        if (items == null) {
            return songs;
        }

        for (JsonElement element : items) {
            JsonObject trackWrapper = element.getAsJsonObject();

            // Some entries may not have "track" (e.g., removed or local tracks)
            if (!trackWrapper.has("track") || trackWrapper.get("track").isJsonNull()) {
                continue;
            }

            JsonObject track = trackWrapper.getAsJsonObject("track");

            // Title
            String title = "Unknown Title";
            if (track.has("name") && !track.get("name").isJsonNull()) {
                title = track.get("name").getAsString();
            }

            // First artist name
            String artist = "Unknown Artist";
            JsonArray artists = track.getAsJsonArray("artists");
            if (artists != null && artists.size() > 0) {
                JsonObject firstArtist = artists.get(0).getAsJsonObject();
                if (firstArtist.has("name") && !firstArtist.get("name").isJsonNull()) {
                    artist = firstArtist.get("name").getAsString();
                }
            }

            // Match your expected format: {"artist": ..., "title": ...}
            JsonObject songObj = new JsonObject();
            songObj.addProperty("artist", artist);
            songObj.addProperty("title", title);

            songs.add(songObj);
        }

        return songs;
    }
}
