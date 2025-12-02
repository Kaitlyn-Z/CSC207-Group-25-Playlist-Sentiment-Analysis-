package app;

import data_access.SpotifyUserPlaylistsDataAccessObject;
import entity.Playlist;
import entity.PlaylistFactory;

import java.io.IOException;
import java.util.List;

public class SpotifyPlaylistDemo {

    public static void main(String[] args) {


        //    String spotifyUserToken = "1POdFZRZbvb...qqillRxMr2z";
        //
        String spotifyUserToken = "1POdFZRZbvb...qqillRxMr2z";

        if (spotifyUserToken.startsWith("PASTE_")) {
            System.out.println("Please paste your manually generated Spotify token.");
            return;
        }

        PlaylistFactory playlistFactory = new PlaylistFactory();
        SpotifyUserPlaylistsDataAccessObject dao =
                new SpotifyUserPlaylistsDataAccessObject(playlistFactory);

        try {
            List<Playlist> playlists = dao.getCurrentUserPlaylists(spotifyUserToken);

            System.out.println("Found " + playlists.size() + " playlists.\n");

            for (Playlist p : playlists) {
                System.out.println("========================================");
                System.out.println("Playlist Name: " + p.getPlaylistName());
                System.out.println("Playlist ID:   " + p.getPlaylistId());
                System.out.println("Songs:");
                p.getSongs().forEach(songElem -> {
                    var songObj = songElem.getAsJsonObject();
                    String artist = songObj.get("artist").getAsString();
                    String title = songObj.get("title").getAsString();
                    System.out.println("  - " + title + " by " + artist);
                });
                System.out.println();
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching playlists from Spotify:");
            e.printStackTrace();
        }
    }
}
