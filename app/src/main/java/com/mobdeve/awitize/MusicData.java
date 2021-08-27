package com.mobdeve.awitize;

import java.util.ArrayList;

public class MusicData {

    private String artist;
    private String title;
    private ArrayList<String> genres;
    private String audioFileURL;
    private String album;
    private String albumCoverURL;
    private boolean banned;

    public MusicData(String artist, String title, String audioFileURL, String albumCoverURL){
        this.artist = artist;
        this.title = title;
//        this.genres = genre;
//        this.album = album;
        this.audioFileURL = audioFileURL;
        this.albumCoverURL = albumCoverURL;
        banned = false;
    }

    public MusicData(String artist, String title, String audioFileURL, String albumCoverURL, boolean banned){
        this.artist = artist;
        this.title = title;
//        this.genres = genre;
//        this.album = album;
        this.audioFileURL = audioFileURL;
        this.albumCoverURL = albumCoverURL;
        this.banned = banned;
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

    public String getAlbumCoverURL() {
        return albumCoverURL;
    }

    public boolean isBanned() {
        return banned;
    }
}
