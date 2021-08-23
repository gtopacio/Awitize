package com.mobdeve.awitize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Iterator;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseUser user;
    private TextView emailView;
    private ArrayList<MusicData> songs;
    private ArrayList<Genre> genres;
    private RecyclerView rvDasboard;
    private GenreAdapter genreAdapter;

    private TextView nowPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkSession();

        setContentView(R.layout.activity_main);
        // FOR TESTING ONLY
        Spinner spinner = findViewById(R.id.sp_category_select);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("GENRE");
        arrayList.add("ARTIST");
        arrayList.add("ALBUM");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tutorialsName = parent.getItemAtPosition(position).toString();
//                Toast.makeText(parent.getContext(), "Selected: " + tutorialsName, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

        nowPlaying = findViewById(R.id.tv_now_playing_main);
        initRecyclerView();
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
                    nowPlaying.setOnClickListener(v -> {
                        Intent i = new Intent(DashboardActivity.this, MusicPlayerActivity.class);
                        MusicData song = songs.get(0);
                        i.putExtra("title", song.getTitle());
                        i.putExtra("artist", song.getArtist());
                        i.putExtra("url", song.getUrl());
                        startActivity(i);
                        finish();
                    });
                }
            }
        });
    }

    private void initRecyclerView() {
        this.genres = GenreDataHelper.loadGenres();

        this.rvDasboard = findViewById(R.id.rv_category_selection);
        this.rvDasboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        this.genreAdapter = new GenreAdapter(this.genres);
        this.rvDasboard.setAdapter(this.genreAdapter);
    }

    private void checkSession(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent i = new Intent(DashboardActivity.this, SigninActivity.class);
            i.putExtra("Illegal Access", true);
            startActivity(i);
            finish();
        }
    }
}