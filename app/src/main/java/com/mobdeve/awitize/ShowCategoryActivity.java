package com.mobdeve.awitize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.awitize.helpers.DatabaseHelper;
import com.mobdeve.awitize.interfaces.QueueSong;
import com.mobdeve.awitize.services.DatabaseUpdater;
import com.mobdeve.awitize.services.PlayerEvents;
import com.mobdeve.awitize.services.PlayerService;
import com.mobdeve.awitize.state.GlobalState;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

public class ShowCategoryActivity extends AppCompatActivity implements QueueSong {

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

    private PlayerService playerService;
    private boolean isServiceBounded = false;

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

    private BroadcastReceiver newSongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    private BroadcastReceiver playerStateChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI();
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayerService.PlayerBinder binder = (PlayerService.PlayerBinder) service;
            playerService = binder.getService();
            isServiceBounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBounded = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_template);

        Intent serviceIntent = new Intent(this, PlayerService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(newSongReceiver, new IntentFilter(PlayerEvents.NEW_SONG.name()));
        LocalBroadcastManager.getInstance(this).registerReceiver(playerStateChanged, new IntentFilter(PlayerEvents.STATE_CHANGED.name()));

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

        playButton.setOnClickListener(v -> {
            if(GlobalState.isIsPlaying()){
                Intent i = new Intent(PlayerEvents.PAUSE.name());
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            }
            else{
                Intent i = new Intent(PlayerEvents.PLAY.name());
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            }
        });

        Intent intent = getIntent();

        categoryName = intent.getStringExtra(CategoryConstants.CATEGORY_NAME.name());
        categoryType = intent.getStringExtra(CategoryConstants.CATEGORY_TYPE.name());

        songs = new ArrayList<>();

        this.rvSongs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.songAdapter = new SongAdapter(this.songs, this);
        this.rvSongs.setAdapter(this.songAdapter);
        this.Title.setText(categoryName);

        databaseHelper.getCategorySongs(categoryType, categoryName);

        LocalBroadcastManager.getInstance(this).registerReceiver(loadSongReceiver, new IntentFilter("Load Music Data"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loadSongReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newSongReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playerStateChanged);
    }

    private void updateUI(){
        MusicData curr = GlobalState.getNowPlaying();
        if(curr != null){
            songArtistName.setText(curr.getTitle());
            songName.setText(curr.getArtist());
            int image = GlobalState.isIsPlaying() ? R.drawable.ic___70_pause_button : R.drawable.ic___72_play_button;
            playButton.setBackgroundResource(image);
        }
    }

    @Override
    public void queueMusic(MusicData musicData) {
        if(isServiceBounded){
            playerService.queueSong(musicData);
        }
        else{
            Toast.makeText(this, "Please wait for service then try again", Toast.LENGTH_SHORT).show();
        }
    }
}