package com.mobdeve.awitize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class MusicPlayerActivity extends AppCompatActivity {

    private TextView currSong;
    private TextView currArtist;
    private SimpleExoPlayer player;
    private StyledPlayerView playerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        Intent i = getIntent();
        String title = i.getStringExtra("title");
        String artist = i.getStringExtra("artist");
        String url = i.getStringExtra("url");

        initComponents();

        currSong.setText(title);
        currArtist.setText(artist);
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        MediaItem song = MediaItem.fromUri(url);
        player.addMediaItem(song);
        player.prepare();
    }

    @Override
    protected void onStart() {
        super.onStart();
        player.play();
    }

    private void initComponents(){
        currSong = findViewById(R.id.tv_curr_song);
        currArtist = findViewById(R.id.tv_curr_artist);
        playerView = findViewById(R.id.pv_player_view);
    }
}