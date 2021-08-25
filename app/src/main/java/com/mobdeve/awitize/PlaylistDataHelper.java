package com.mobdeve.awitize;

import java.util.ArrayList;

public class PlaylistDataHelper {
    public static ArrayList<Playlist> loadPlaylist () {
        ArrayList<Playlist> playlists = new ArrayList<>();

        playlists.add(new Playlist("Bop"));
        playlists.add(new Playlist("RJ Playlist"));
        playlists.add(new Playlist("Geoff Playlist"));
        playlists.add(new Playlist("Aaron Playlist"));

        return playlists;
    }
}
