package com.mobdeve.awitize;

import java.util.ArrayList;

public class GenreDataHelper {
    public static ArrayList<Genre> loadGenres () {
        ArrayList<Genre> genres = new ArrayList<>();

        genres.add(new Genre("Rock", 0));
        genres.add(new Genre("Jazz", 0));
        genres.add(new Genre("Dance", 0));
        genres.add(new Genre("Pop", 0));
        genres.add(new Genre("Country", 0));

        return genres;
    }
}
