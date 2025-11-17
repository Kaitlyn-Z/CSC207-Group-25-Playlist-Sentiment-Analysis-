package data_access;

import entity.PlaylistFactory;
//import related data access interface


//Template in DBUserDataAccessObject Class
public class DBPlaylistDataAccessObject { //implements related data access interface


    private final PlaylistFactory playlistFactory;


    public DBPlaylistDataAccessObject(PlaylistFactory playlistFactory) {
        this.playlistFactory = playlistFactory;
    }
}
