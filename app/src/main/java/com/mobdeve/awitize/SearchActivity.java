package com.mobdeve.awitize;

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
import android.os.IBinder;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.mobdeve.awitize.services.PlayerEvents;
import com.mobdeve.awitize.services.PlayerService;
import com.mobdeve.awitize.state.GlobalState;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ArrayList<MusicData> songsResult;
    private ConstraintLayout clMusicPlayer;
    private TextView nowPlayingTitle;
    private TextView nowPlayingArtist;
    private ImageButton playPauseButton;
    private ImageButton nextButton;
    private RecyclerView searchRvSongs;
    private SearchSongAdapter searchSongAdapter;
    private SearchView searchToolbar;

    private PlayerService playerService;
    private boolean isServiceBounded = false;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent serviceIntent = new Intent(this, PlayerService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(newSongReceiver, new IntentFilter(PlayerEvents.NEW_SONG.name()));
        LocalBroadcastManager.getInstance(this).registerReceiver(playerStateChanged, new IntentFilter(PlayerEvents.STATE_CHANGED.name()));

        this.clMusicPlayer = findViewById(R.id.cl_music_player_search);
        this.nowPlayingTitle = findViewById(R.id.tv_now_playing_search_title);
        this.nowPlayingArtist = findViewById(R.id.tv_now_playing_search_artist);
        this.playPauseButton = findViewById(R.id.ib_play_search);
        this.searchRvSongs = findViewById(R.id.rv_search_selection);
        this.searchToolbar = findViewById(R.id.sv_toolbar);
        this.nextButton = findViewById(R.id.ib_next_search);

        songsResult = new ArrayList<>();

        songsResult.add(new MusicData("Some Playable Artist", "Playable Song", "123", "123"));
        songsResult.add(new MusicData("Some Not Playable Artist", "Region Locked Song", "12345", "12345", true));

        this.searchRvSongs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.searchSongAdapter = new SearchSongAdapter(this.songsResult);
        this.searchRvSongs.setAdapter(this.searchSongAdapter);

        playPauseButton.setOnClickListener(v -> {
            if(GlobalState.isIsPlaying()){
                Intent i = new Intent(PlayerEvents.PAUSE.name());
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            }
            else{
                Intent i = new Intent(PlayerEvents.PLAY.name());
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            }
        });

        nextButton.setOnClickListener(v -> {
            Intent i = new Intent(PlayerEvents.SKIP.name());
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newSongReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playerStateChanged);
        unbindService(connection);

    }

    private void updateUI(){
        MusicData curr = GlobalState.getNowPlaying();
        if(curr != null){
            nowPlayingTitle.setText(curr.getTitle());
            nowPlayingArtist.setText(curr.getArtist());
            int image = GlobalState.isIsPlaying() ? R.drawable.ic___70_pause_button : R.drawable.ic___72_play_button;
            playPauseButton.setBackgroundResource(image);
        }
        else{
            nowPlayingTitle.setText("No Song");
            nowPlayingArtist.setText("No Artist");
            int image = GlobalState.isIsPlaying() ? R.drawable.ic___70_pause_button : R.drawable.ic___72_play_button;
            playPauseButton.setBackgroundResource(image);
        }
    }
}