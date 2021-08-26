package com.mobdeve.awitize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.awitize.helpers.DatabaseHelper;
import com.mobdeve.awitize.services.DatabaseUpdater;
import com.mobdeve.awitize.state.GlobalState;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

public class ShowCategoryActivity extends AppCompatActivity {

    private ArrayList<MusicData> songs;
    private ConstraintLayout clMusicPlayer;
    private TextView songName;
    private TextView songArtistName;
    private ImageButton playButton;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private RecyclerView rvSongs;
    private SongAdapter songAdapter;
    private TextView Title;
    private ImageButton back;

    private String categoryName;
    private String categoryType;

    private DatabaseHelper databaseHelper;

    private BroadcastReceiver loadSongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String artist = intent.getStringExtra("artist");
            String title = intent.getStringExtra("title");
            String audioFileURL = intent.getStringExtra("audioFileURL");
            String albumCoverURL = intent.getStringExtra("albumCoverURL");
            songs.add(new MusicData(artist, title, audioFileURL, albumCoverURL));
            songAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_template);

        this.clMusicPlayer = findViewById(R.id.cl_music_player_cat);
        this.songName = findViewById(R.id.tv_now_playing_cat_title);
        this.songArtistName = findViewById(R.id.tv_now_playing_cat_artist);
        this.playButton = findViewById(R.id.ib_play_cat);
        this.prevButton = findViewById(R.id.ib_prev_cat);
        this.nextButton = findViewById(R.id.ib_next_cat);
        this.rvSongs = findViewById(R.id.rv_content_selection);
        this.Title = findViewById(R.id.tv_category_type);
        this.back = findViewById(R.id.ib_back_cat);

        databaseHelper = new DatabaseHelper(this);

        back.setOnClickListener(v -> {
            this.onBackPressed();
        });

        Intent intent = getIntent();

        categoryName = intent.getStringExtra(CategoryConstants.CATEGORY_NAME.name());
        categoryType = intent.getStringExtra(CategoryConstants.CATEGORY_TYPE.name());

        songs = new ArrayList<>();

        this.rvSongs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.songAdapter = new SongAdapter(this.songs);
        this.rvSongs.setAdapter(this.songAdapter);
        this.Title.setText(categoryName);

        databaseHelper.getCategorySongs(categoryType, categoryName);

        LocalBroadcastManager.getInstance(this).registerReceiver(loadSongReceiver, new IntentFilter("Load Music Data"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loadSongReceiver);
    }
}