package com.mobdeve.awitize;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.awitize.services.PlayerEvents;
import com.mobdeve.awitize.services.PlayerService;
import com.mobdeve.awitize.state.GlobalState;

import java.util.ArrayList;
import java.util.Iterator;

public class MyLibraryActivity extends AppCompatActivity {
    private FirebaseUser user;
    private ArrayList<Playlist> playlists;
    private RecyclerView rvDasboard;
    private PlaylistAdapter playlistAdapter;
    private FloatingActionButton pageSelect;
    private ImageButton accountButton;

    private TextView nowPlayingTitle;
    private TextView nowPlayingArtist;
    private ImageButton playPauseButton;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        checkSession();
        loadComponents();

        Intent serviceIntent = new Intent(this, PlayerService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(newSongReceiver, new IntentFilter(PlayerEvents.NEW_SONG.name()));
        LocalBroadcastManager.getInstance(this).registerReceiver(playerStateChanged, new IntentFilter(PlayerEvents.STATE_CHANGED.name()));

        initRecyclerView();
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

    private void loadComponents() {
        nowPlayingTitle = findViewById(R.id.tv_now_playing_lib_title);
        nowPlayingArtist = findViewById(R.id.tv_now_playing_lib_artist);
        playPauseButton = findViewById(R.id.ib_play_lib);
        pageSelect = findViewById(R.id.fab_page_select_lib);
        accountButton = findViewById(R.id.ib_account_lib);

        nowPlayingTitle.setOnClickListener(v -> {
            Intent i = new Intent(MyLibraryActivity.this, MusicPlayerActivity.class);
            i.putExtra(IntentKeys.PREVIOUS_CLASS.name(), this.getClass().getName());
            startActivity(i);
            finish();
        });

        pageSelect.setOnClickListener(v -> {
            Intent i = new Intent(MyLibraryActivity.this, DashboardActivity.class);
            startActivity(i);
            finish();
        });

        accountButton.setOnClickListener(v -> {
            Intent i = new Intent(MyLibraryActivity.this, AccountActivity.class);
            i.putExtra(IntentKeys.PREVIOUS_CLASS.name(), this.getClass().getName());
            startActivity(i);
            finish();
        });

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
    }

    private void initRecyclerView() {
        this.playlists = PlaylistDataHelper.loadPlaylist();

        this.rvDasboard = findViewById(R.id.rv_library_selection);
        this.rvDasboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        this.playlistAdapter = new PlaylistAdapter(this.playlists);
        this.rvDasboard.setAdapter(this.playlistAdapter);
    }

    private void checkSession() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent i = new Intent(MyLibraryActivity.this, SigninActivity.class);
            i.putExtra("Illegal Access", true);
            startActivity(i);
            finish();
        }
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
