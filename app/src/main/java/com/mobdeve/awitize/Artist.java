package com.mobdeve.awitize;

public class Artist {

    private String artistName;
    private int artistNumOfSongs;

    public Artist (String name, int numofsongs) {
        this.artistName = name;
        this.artistNumOfSongs = numofsongs;
    }

    public String getArtistName () {
        return this.artistName;
    }

    public int getArtistNumOfSongs() {
        return this.artistNumOfSongs;
    }
}
