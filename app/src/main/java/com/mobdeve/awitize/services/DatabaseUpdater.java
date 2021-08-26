package com.mobdeve.awitize.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.awitize.Album;
import com.mobdeve.awitize.Artist;
import com.mobdeve.awitize.DatabaseKeys;
import com.mobdeve.awitize.Genre;
import com.mobdeve.awitize.state.GlobalState;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

public class DatabaseUpdater extends Service {

    private DatabaseReference albums;
    private DatabaseReference artists;
    private DatabaseReference genres;

    private static final String TAG = "DatabaseUpdater";


    public DatabaseUpdater() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://awitize-d10e3-default-rtdb.asia-southeast1.firebasedatabase.app");

        albums = db.getReference(DatabaseKeys.albums.name());
        artists = db.getReference(DatabaseKeys.artists.name());
        genres = db.getReference(DatabaseKeys.genres.name());

        albums.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<Album> newAlbumsMetaData = new ArrayList<>();
                Iterator<DataSnapshot> it = snapshot.getChildren().iterator();
                while(it.hasNext()){
                    DataSnapshot curr = it.next();
                    Log.d(TAG, "onDataChange: Read " + curr.getKey() + " - " + curr.getChildrenCount());
                    newAlbumsMetaData.add(new Album(curr.getKey(), (int) curr.getChildrenCount()));
                }
                GlobalState.setAlbums(newAlbumsMetaData);
                Intent i = new Intent("Albums Updated");
                LocalBroadcastManager.getInstance(DatabaseUpdater.this).sendBroadcast(i);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.d(TAG, "onCancelled: Unable to read albums" + error.getMessage());
            }
        });

        artists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<Artist> newArtists = new ArrayList<>();
                Iterator<DataSnapshot> it = snapshot.getChildren().iterator();
                while(it.hasNext()){
                    DataSnapshot curr = it.next();
                    Log.d(TAG, "onDataChange: Read " + curr.getKey() + " - " + curr.getChildrenCount());
                    newArtists.add(new Artist(curr.getKey(), (int) curr.getChildrenCount()));
                }
                GlobalState.setArtists(newArtists);
                Intent i = new Intent("Artists Updated");
                LocalBroadcastManager.getInstance(DatabaseUpdater.this).sendBroadcast(i);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.d(TAG, "onCancelled: Unable to read albums" + error.getMessage());
            }
        });

        genres.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<Genre> newGenres = new ArrayList<>();
                Iterator<DataSnapshot> it = snapshot.getChildren().iterator();
                while(it.hasNext()){
                    DataSnapshot curr = it.next();
                    Log.d(TAG, "onDataChange: Read " + curr.getKey() + " - " + curr.getChildrenCount());
                    newGenres.add(new Genre(curr.getKey(), (int) curr.getChildrenCount()));
                }
                GlobalState.setGenres(newGenres);
                Intent i = new Intent("Genres Updated");
                LocalBroadcastManager.getInstance(DatabaseUpdater.this).sendBroadcast(i);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.d(TAG, "onCancelled: Unable to read albums" + error.getMessage());
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}