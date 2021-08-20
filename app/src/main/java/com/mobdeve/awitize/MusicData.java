package com.mobdeve.awitize;

public class MusicData {

    private String artist;
    private String title;
    private String genre;
    private String url;
    private String album;

    public MusicData(String artist, String title, String url, String genre, String album){
        this.artist = artist;
        this.title = title;
        this.genre = genre;
        this.album = album;
        this.url = url;
    }


    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getGenre() {return genre;}

    public String getAlbum() {return album;}
}
