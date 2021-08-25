package com.mobdeve.awitize;

public class Album {

    private  String albumName;
    private int albumNumOfSongs;

    public Album (String albumName, int albumNumOfSongs) {
        this.albumName = albumName;
        this.albumNumOfSongs = albumNumOfSongs;
    }

    public String getAlbumName () {
        return this.albumName;
    }

    public int getAlbumNumOfSongs () {
        return this.albumNumOfSongs;
    }
}
