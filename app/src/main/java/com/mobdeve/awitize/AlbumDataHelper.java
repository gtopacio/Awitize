package com.mobdeve.awitize;

import java.util.ArrayList;

public class AlbumDataHelper {
    public static ArrayList<Album> loadAlbums () {
        ArrayList<Album> albums = new ArrayList<>();

        albums.add(new Album("2017", 12));
        albums.add(new Album("Album 2", 25));

        return albums;
    }
}
