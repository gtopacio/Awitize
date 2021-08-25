package com.mobdeve.awitize;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.Iterator;

public class MyLibraryActivity extends AppCompatActivity {
    private FirebaseUser user;
    private TextView emailView;
    private ArrayList<MusicData> songs;
    private ArrayList<Playlist> playlists;
    private RecyclerView rvDasboard;
    private PlaylistAdapter playlistAdapter;
    private TextView nowPlaying;
    private FloatingActionButton pageSelect;
    private ImageButton accountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        checkSession();

        loadComponents();
//        // FOR TESTING ONLY
//        Spinner spinner = findViewById(R.id.sp_category_select);
//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add("SONGS");
//        arrayList.add("ALBUM");
//        arrayList.add("GENRE");
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(arrayAdapter);
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String tutorialsName = parent.getItemAtPosition(position).toString();
////                Toast.makeText(parent.getContext(), "Selected: " + tutorialsName, Toast.LENGTH_LONG).show();
//            }
//            @Override
//            public void onNothingSelected(AdapterView <?> parent) {
//            }
//        });

        initRecyclerView();
    }

    private void loadComponents() {
        nowPlaying = findViewById(R.id.tv_now_playing_lib);
        pageSelect = findViewById(R.id.fab_page_select_lib);
        accountButton = findViewById(R.id.ib_account_lib);

        nowPlaying.setOnClickListener(v -> {
            Intent i = new Intent(MyLibraryActivity.this, MusicPlayerActivity.class);
            MusicData song = songs.get(0);
            i.putExtra(SongAttributes.TITLE.name(), song.getTitle());
            i.putExtra(SongAttributes.ARTIST.name(), song.getArtist());
            i.putExtra(SongAttributes.URL.name(), song.getUrl());
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference music = FirebaseDatabase.getInstance("https://awitize-d10e3-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("music");
        music.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Iterator<DataSnapshot> retSongs = task.getResult().getChildren().iterator();
                    songs = new ArrayList<>();
                    while(retSongs.hasNext()){
                        DataSnapshot d = retSongs.next();
                        String artist = String.valueOf(d.child("artist").getValue());
                        String title = String.valueOf(d.child("title").getValue());
                        String url = String.valueOf(d.child("url").getValue());
                        String genre = String.valueOf(d.child("genre").getValue());
                        String album = String.valueOf(d.child("album").getValue());
                        Log.w("Loaded", artist + " - " + title + ", " + genre + " " + album + " " + url);
                        songs.add(new MusicData(artist, title, url, genre, album));
                    }
                }
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
}
