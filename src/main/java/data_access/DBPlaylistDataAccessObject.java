package data_access;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entity.PlaylistFactory;
import use_case.analyze_playlist.SpotifyPlaylistDataAccessInterface;

import java.net.URLEncoder;
import java.util.concurrent.ThreadLocalRandom;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class DBPlaylistDataAccessObject implements SpotifyPlaylistDataAccessInterface {


    private final PlaylistFactory playlistFactory;
    private static final int MAX_SONGS = 5;

    public DBPlaylistDataAccessObject(PlaylistFactory playlistFactory) {this.playlistFactory = playlistFactory;}

    @Override
    public JsonArray getLyrics(JsonArray songs) {
        final JsonArray songsInfo = new JsonArray();
        final JsonArray songsCopy = songs.deepCopy();
        HttpClient client = HttpClient.newHttpClient();

        while (songsCopy.size() != 0 && songsInfo.size() < MAX_SONGS) {
            int index = ThreadLocalRandom.current().nextInt(0, songsCopy.size());
            JsonObject song = songsCopy.get(index).getAsJsonObject();
            songsCopy.remove(index);
            String artist = song.get("artist").getAsString();
            String title = song.get("title").getAsString();

            try {
                String artistCopy = URLEncoder.encode(artist, "UTF-8");
                String titleCopy = URLEncoder.encode(title, "UTF-8");

                String url = String.format("https://api.lyrics.ovh/v1/%s/%s", artistCopy, titleCopy);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {continue;}

                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

                if (json.has("error")) {continue;}

                String lyrics = json.get("lyrics").getAsString();
                if (lyrics == null || lyrics.isBlank()) {continue;}

                JsonObject songInfo = new JsonObject();
                songInfo.addProperty("artist", artist);
                songInfo.addProperty("title", title);
                songInfo.addProperty("lyrics", lyrics);
                songsInfo.add(songInfo);

            } catch (Exception ignored) {
            }
        }
        return songsInfo;
    }

}
