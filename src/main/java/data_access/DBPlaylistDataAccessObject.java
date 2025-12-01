package data_access;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import entity.PlaylistFactory;
import use_case.analyze_playlist.SpotifyPlaylistDataAccessInterface;

public class DBPlaylistDataAccessObject implements SpotifyPlaylistDataAccessInterface {

    private static final int MAX_SONGS = 5;
    private static final int SUCCESS_CODE = 200;
    private final PlaylistFactory playlistFactory;

    public DBPlaylistDataAccessObject(PlaylistFactory playlistFactory) {
        this.playlistFactory = playlistFactory;
    }

    @Override
    public JsonArray getLyrics(JsonArray songs) {
        final JsonArray songsInfo = new JsonArray();
        final JsonArray songsCopy = songs.deepCopy();
        final HttpClient client = HttpClient.newHttpClient();

        while (songsCopy.size() != 0 && songsInfo.size() < MAX_SONGS) {
            final int index = ThreadLocalRandom.current().nextInt(0, songsCopy.size());
            final JsonObject song = songsCopy.get(index).getAsJsonObject();
            songsCopy.remove(index);
            final String artist = song.get("artist").getAsString();
            final String title = song.get("title").getAsString();

            try {
                final String artistCopy = URLEncoder.encode(artist, "UTF-8");
                final String titleCopy = URLEncoder.encode(title, "UTF-8");

                final String url = String.format("https://api.lyrics.ovh/v1/%s/%s", artistCopy, titleCopy);

                final HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .header("Content-Type", "application/json")
                        .build();

                final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != SUCCESS_CODE) {
                    continue;
                }

                final JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

                if (json.has("error")) {
                    continue;
                }

                final String lyrics = json.get("lyrics").getAsString();
                if (lyrics == null || lyrics.isBlank()) {
                    continue;
                }

                final JsonObject songInfo = new JsonObject();
                songInfo.addProperty("artist", artist);
                songInfo.addProperty("title", title);
                songInfo.addProperty("lyrics", lyrics);
                songsInfo.add(songInfo);

            }
            catch (IOException | InterruptedException | JsonSyntaxException | IllegalStateException error) {
            // ignore errors and continue
            }
        }
        return songsInfo;
    }

    @Override
    public String getStringLyrics(JsonArray songsInfo) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < songsInfo.size(); i++) {
            final String lyric = songsInfo.get(i)
                                .getAsJsonObject()
                                .get("lyrics")
                                .getAsString();

            builder.append(lyric).append("\n");
        }
        return builder.toString();
    }

}
