package data_access;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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


//Template in DBUserDataAccessObject Class
public class DBPlaylistDataAccessObject implements SpotifyPlaylistDataAccessInterface {


    private final PlaylistFactory playlistFactory;
    private static final int MAX_SONGS = 5;

    public DBPlaylistDataAccessObject(PlaylistFactory playlistFactory) {
        this.playlistFactory = playlistFactory;
    }

    @Override
    public JsonArray getLyrics(JsonArray songs){
        final JsonArray songsInfo = new JsonArray();
        final JsonArray songsCopy = songs.deepCopy();
        if(songs.size() > 0) {
            while (songsCopy.size() != 0 && songsInfo.size() < MAX_SONGS) {
                JsonObject song = getRandomSong(songsCopy).getAsJsonObject();
                songsCopy.remove(song);
                String artist = song.get("artist").getAsString();
                String title = song.get("title").getAsString();

                //derive lyrics from lyrics api
                try {
                    String artistCopy = URLEncoder.encode(artist, "UTF-8");
                    String titleCopy = URLEncoder.encode(title, "UTF-8");

                    String url = String.format("https://api.lyrics.ovh/v1/%s/%s", artistCopy, titleCopy);

                    HttpClient client = HttpClient.newHttpClient();

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .header("Content-Type", "application/json")
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

                    if (json.has("lyrics")) {
                        String lyrics = json.get("lyrics").getAsString();
                        JsonObject songInfo = new JsonObject();
                        songInfo.addProperty("artist", artist);
                        songInfo.addProperty("title", title);
                        songInfo.addProperty("lyrics", lyrics);
                        songsInfo.add(songInfo);
                        }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if(songsInfo.size() == 0) {
                throw new RuntimeException("No lyrics found");
            }
        }
        else{
            throw new RuntimeException("No songs in playlist");
        }
        return songsInfo;
    }

    public JsonElement getRandomSong(JsonArray songs) {
        int index = ThreadLocalRandom.current().nextInt(songs.size());
        return songs.get(index);
    }
}
