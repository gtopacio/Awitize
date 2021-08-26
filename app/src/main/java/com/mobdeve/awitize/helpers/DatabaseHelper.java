package com.mobdeve.awitize.helpers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.awitize.DatabaseKeys;
import com.mobdeve.awitize.Genre;
import com.mobdeve.awitize.MusicData;
import com.mobdeve.awitize.services.DatabaseUpdater;
import com.mobdeve.awitize.state.GlobalState;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

public class DatabaseHelper {

    private Context context;
    private FirebaseDatabase db;
    private DatabaseReference music;
    private DatabaseReference album;
    private DatabaseReference artist;
    private DatabaseReference genre;

    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context){
        this.context = context;
        db = FirebaseDatabase.getInstance("https://awitize-d10e3-default-rtdb.asia-southeast1.firebasedatabase.app");
        music = db.getReference(DatabaseKeys.music.name());
        album = db.getReference(DatabaseKeys.albums.name());
        artist = db.getReference(DatabaseKeys.artists.name());
        genre = db.getReference(DatabaseKeys.genres.name());
    }

    public void loadMusicData(String musicID){
        DatabaseReference ref = music.child(musicID);
        ref.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DataSnapshot snapshot = task.getResult();
                String artist = (String) snapshot.child(DatabaseKeys.artist.name()).getValue();
                String title = (String) snapshot.child(DatabaseKeys.title.name()).getValue();
                String audioFileURL = (String) snapshot.child(DatabaseKeys.audioFileURL.name()).getValue();
                String albumCoverURL = (String) snapshot.child(DatabaseKeys.albumCoverURL.name()).getValue();
                Intent i = new Intent("Load Music Data");
                i.putExtra("artist", artist);
                i.putExtra("title", title);
                i.putExtra("audioFileURL", audioFileURL);
                i.putExtra("albumCoverURL", albumCoverURL);
                Log.d(TAG, "loadMusicData: Broadcasting " + artist + " - " + title);
                LocalBroadcastManager.getInstance(context).sendBroadcast(i);
            }
        });
    }

    public void getCategorySongs(String type, String name){
        DatabaseReference ref = null;
        switch(type){
            case "ALBUM":
                ref = album;
                break;
            case "GENRE":
                ref = genre;
                break;
            case "ARTIST":
                ref = artist;
                break;
        }

        ref = ref.child(name);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Iterator<DataSnapshot> it = snapshot.getChildren().iterator();
                while(it.hasNext()){
                    DataSnapshot snapshot1 = it.next();
                    Log.d(TAG, "onDataChange: ID - " + snapshot1.getKey());
                    loadMusicData((String) snapshot1.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
