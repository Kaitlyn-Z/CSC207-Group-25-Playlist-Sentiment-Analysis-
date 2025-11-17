package data_access;

import entity.SongFactory;
//import related data access interface


//Template in DBUserDataAccessObject Class
public class DBSongDataAccessObject { //implements related data access interface

    private final SongFactory songFactory;

    public DBSongDataAccessObject(SongFactory songFactory) {
        this.songFactory = songFactory;
    }
}
