package com.mobdeve.awitize;

import java.util.ArrayList;

public class ArtistDataHelper {
    public static ArrayList<Artist> loadArtist () {
        ArrayList<Artist> artists = new ArrayList<>();

        artists.add(new Artist("HotPause", 10));
        artists.add(new Artist("Bugoy na KoyKoy2", 25));
        artists.add(new Artist("NeVeR", 0));
        artists.add(new Artist("RJCsC", 100));
        artists.add(new Artist("Kalborgers", 12345));
        artists.add(new Artist("Jatot", 420));
        artists.add(new Artist("Noiseless World", 30));

        return artists;
    }
}
