package com.mobdeve.awitize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerControlView;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class MusicPlayerActivity extends AppCompatActivity {

    private TextView currSong;
    private TextView currArtist;
    private String title;
    private String artist;
    private String url;
    private ImageButton back;
    private String prevClass;
//    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        Intent i = getIntent();
        title = i.getStringExtra(SongAttributes.TITLE.name());
        artist = i.getStringExtra(SongAttributes.ARTIST.name());
        url = i.getStringExtra(SongAttributes.URL.name());
        prevClass = i.getStringExtra(IntentKeys.PREVIOUS_CLASS.name());

        initComponents();
//        player = new SimpleExoPlayer.Builder(this).build();
//
//        MediaItem song = MediaItem.fromUri(url);
//        player.addMediaItem(song);
//        player.prepare();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        player.play();
    }

    private void initComponents(){
        currSong = findViewById(R.id.tv_curr_song);
        currArtist = findViewById(R.id.tv_curr_artist);
        back = findViewById(R.id.ib_back_player);
        currSong.setText(title);
        currArtist.setText(artist);

        back.setOnClickListener(v -> {
            try {
                Intent i = new Intent(MusicPlayerActivity.this, Class.forName(prevClass));
                startActivity(i);
                finish();
            }
            catch (ClassNotFoundException ex) {
                Log.d("Error", "Class Not Found");
                Toast.makeText(this,"Error Class Not Found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}