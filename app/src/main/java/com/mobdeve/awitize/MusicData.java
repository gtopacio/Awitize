package com.mobdeve.awitize;

import java.util.ArrayList;

public class MusicData {

    private String artist;
    private String title;
    private ArrayList<String> genres;
    private String audioFileURL;
    private String album;

    public MusicData(String artist, String title, String url, ArrayList<String> genre, String album){
        this.artist = artist;
        this.title = title;
        this.genres = genre;
        this.album = album;
        this.audioFileURL = url;
    }


    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return audioFileURL;
    }

    public ArrayList<String> getGenres() {return genres;}

    public String getAlbum() {return album;}
}
