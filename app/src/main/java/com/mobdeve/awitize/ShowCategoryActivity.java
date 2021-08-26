package com.mobdeve.awitize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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

        Intent intent = getIntent();

        String name = intent.getStringExtra(GenreAdapter.KEY_NAME);

        this.songs = loadSongs();
        this.rvSongs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        this.songAdapter = new SongAdapter(this.songs);
        this.rvSongs.setAdapter(this.songAdapter);

        this.Title.setText(name);



//        DatabaseReference db = FirebaseDatabase.getInstance("https://awitize-d10e3-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("genres");

//        DatabaseReference songs = db.child(name);

//        songs.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                ArrayList<MusicData> genreSongs = new ArrayList<>();
//                Iterator<DataSnapshot> it = snapshot.getChildren().iterator();
//                while(it.hasNext()){
//                    DataSnapshot curr = it.next();
//                    genreSongs.add(new MusicData(curr.getKey(), (int) curr.getChildrenCount()));
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        })



    }

    private ArrayList<MusicData> loadSongs() {
        ArrayList<MusicData> genreSongs = new ArrayList<>();

        genreSongs.add(new MusicData("hello", "song 1", "asd", new ArrayList<String>(), "asdf"));
        genreSongs.add(new MusicData("world", "song 2", "asd", new ArrayList<String>(), "asdf"));
        genreSongs.add(new MusicData("my friend", "song 3", "asd", new ArrayList<String>(), "asdf"));

        return genreSongs;
    }
}