package com.mobdeve.awitize;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ArrayList<MusicData> songsResult;
    private ConstraintLayout clMusicPlayer;
    private TextView searchSongName;
    private TextView searchSongArtistName;
    private ImageButton searchPlayButton;
    private RecyclerView searchRvSongs;
    private SearchSongAdapter searchSongAdapter;
    private SearchView searchToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        this.clMusicPlayer = findViewById(R.id.cl_music_player_search);
        this.searchSongName = findViewById(R.id.tv_now_playing_search_title);
        this.searchSongArtistName = findViewById(R.id.tv_now_playing_search_artist);
        this.searchPlayButton = findViewById(R.id.ib_play_search);
        this.searchRvSongs = findViewById(R.id.rv_search_selection);
        this.searchToolbar = findViewById(R.id.sv_toolbar);

        songsResult = new ArrayList<>();

        songsResult.add(new MusicData("Some Playable Artist", "Playable Song", "123", "123"));
        songsResult.add(new MusicData("Some Not Playable Artist", "Region Locked Song", "12345", "12345"));

        this.searchRvSongs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.searchSongAdapter = new SearchSongAdapter(this.songsResult);
        this.searchRvSongs.setAdapter(this.searchSongAdapter);

    }
}