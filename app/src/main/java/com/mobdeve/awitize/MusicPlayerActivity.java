package com.mobdeve.awitize;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mobdeve.awitize.services.PlayerEvents;
import com.mobdeve.awitize.services.PlayerService;
import com.mobdeve.awitize.state.GlobalState;

public class MusicPlayerActivity extends AppCompatActivity {

    private static final String TAG = "MusicPlayerActivity";
    private TextView nowPlayingTitle;
    private TextView nowPlayingArtist;
    private ImageButton back;
    private ImageButton accountButton;
    private String prevClass;
    private ImageButton playPauseButton;
    private ImageButton nextButton;
    private ImageView albumCover;

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

        Intent serviceIntent = new Intent(this, PlayerService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        Intent i = getIntent();
        prevClass = i.getStringExtra(IntentKeys.PREVIOUS_CLASS.name());

        setContentView(R.layout.activity_music_player);

        initComponents();
        LocalBroadcastManager.getInstance(this).registerReceiver(newSongReceiver, new IntentFilter(PlayerEvents.NEW_SONG.name()));
        LocalBroadcastManager.getInstance(this).registerReceiver(playerStateChanged, new IntentFilter(PlayerEvents.STATE_CHANGED.name()));
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

    private void initComponents(){
        nowPlayingTitle = findViewById(R.id.tv_curr_song);
        nowPlayingArtist = findViewById(R.id.tv_curr_artist);
        back = findViewById(R.id.ib_back_player);
        accountButton = findViewById(R.id.ib_account_player);
        playPauseButton = findViewById(R.id.ib_play_player);
        albumCover = findViewById(R.id.iv_curr_song_pic);
        nextButton = findViewById(R.id.ib_next_player);

        back.setOnClickListener(v -> {
            try {
                if(prevClass == null){
                    Log.d("Error", "Class Not Found");
                    Intent i = new Intent(MusicPlayerActivity.this, DashboardActivity.class);
                    startActivity(i);
                    finish();
                    return;
                }
                Intent i = new Intent(MusicPlayerActivity.this, Class.forName(prevClass));
                startActivity(i);
                finish();
            }
            catch (ClassNotFoundException ex) {
                Log.d("Error", "Class Not Found");
                Intent i = new Intent(MusicPlayerActivity.this, DashboardActivity.class);
                startActivity(i);
                finish();
            }
        });

        playPauseButton.setOnClickListener(v -> {
            if(GlobalState.isIsPlaying()){
                Intent i = new Intent(PlayerEvents.PAUSE.name());
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            }
            else {
                Intent i = new Intent(PlayerEvents.PLAY.name());
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            }
        });

        accountButton.setOnClickListener(v -> {
            Intent i = new Intent(this, AccountActivity.class);
            startActivity(i);
            finish();
        });

        nextButton.setOnClickListener(v -> {
            Intent i = new Intent(PlayerEvents.SKIP.name());
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        });
    }

    private void updateUI(){
        MusicData curr = GlobalState.getNowPlaying();
        if(curr != null){
            Log.d(TAG, "updateUI: Now Playing State " + curr.getArtist() + " - " + curr.getTitle());
            nowPlayingTitle.setText(curr.getTitle());
            nowPlayingArtist.setText(curr.getArtist());
            int image = GlobalState.isIsPlaying() ? R.drawable.ic___70_pause_button : R.drawable.ic___72_play_button;
            playPauseButton.setBackgroundResource(image);
            Glide.with(this).load(curr.getAlbumCoverURL()).error(R.drawable.cover___sample_song_art).into(albumCover);
        }
        else{
            nowPlayingTitle.setText("No Song");
            nowPlayingArtist.setText("No Artist");
            int image = GlobalState.isIsPlaying() ? R.drawable.ic___70_pause_button : R.drawable.ic___72_play_button;
            playPauseButton.setBackgroundResource(image);
            Glide.with(this).load(R.drawable.cover___sample_song_art).into(albumCover);
        }
    }
}