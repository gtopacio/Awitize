package com.mobdeve.awitize;

import java.util.ArrayList;

public class Playlist {

    private String playlistName;
    private int numOfSongs;
    private ArrayList<MusicData> musics;

    public Playlist(String playlistName) {
        this.playlistName = playlistName;
        this.numOfSongs = 0;
    }

    public Playlist(String playlistName, MusicData musicData) {
        this.playlistName = playlistName;
        musics.add(musicData);
        this.numOfSongs = 1;
    }

    public void addSong(MusicData musicData) {
        musics.add(musicData);
        numOfSongs++;
    }

    public String getPlaylistName() {
        return this.playlistName;
    }

    public int getNumOfSongs() {
        return this.numOfSongs;
    }

    public ArrayList<MusicData> getMusics () {
        return this.musics;
    }
}
