package com.mobdeve.awitize;

public class MusicData {

    private String artist;
    private String title;
    private String url;

    public MusicData(String artist, String title, String url){
        this.artist = artist;
        this.title = title;
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
}
