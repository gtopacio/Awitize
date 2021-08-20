package com.mobdeve.awitize;

public class Genre {

    private String genre;
    private int numOfSongs;

    public Genre (String genre, int numofsongs) {
        this.genre = genre;
        this.numOfSongs = numofsongs;
    }

    public String getGenre () {
        return this.genre;
    }

    public int getNumOfSongs () {
        return this.numOfSongs;
    }


}
