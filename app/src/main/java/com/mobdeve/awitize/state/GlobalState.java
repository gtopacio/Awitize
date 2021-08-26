package com.mobdeve.awitize.state;

import com.mobdeve.awitize.Album;
import com.mobdeve.awitize.Artist;
import com.mobdeve.awitize.Genre;
import com.mobdeve.awitize.MusicData;

import java.util.ArrayList;

public class GlobalState {

    private static MusicData nowPlaying;
    private static boolean isPlaying = false;
    private static ArrayList<Album> albums;
    private static ArrayList<Genre> genres;
    private static ArrayList<Artist> artists;

    public static void setNowPlaying(MusicData musicData){
        nowPlaying = musicData;
    }

    public static MusicData getNowPlaying(){
        return nowPlaying;
    }

    public static boolean isIsPlaying() {
        return isPlaying;
    }

    public static void setIsPlaying(boolean isPlaying) {
        GlobalState.isPlaying = isPlaying;
    }

    public static ArrayList<Album> getAlbums() {
        return albums;
    }

    public static void setAlbums(ArrayList<Album> albumsMetaData) {
        GlobalState.albums = albumsMetaData;
    }

    public static ArrayList<Genre> getGenres() {
        return genres;
    }

    public static void setGenres(ArrayList<Genre> genres) {
        GlobalState.genres = genres;
    }

    public static ArrayList<Artist> getArtists() {
        return artists;
    }

    public static void setArtists(ArrayList<Artist> artists) {
        GlobalState.artists = artists;
    }
}
