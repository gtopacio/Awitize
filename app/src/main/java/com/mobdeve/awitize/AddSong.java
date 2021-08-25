package com.mobdeve.awitize;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class AddSong extends AppCompatActivity {

    private FirebaseDatabase mDatabase;

    private static final String TAG = "AddSong";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        mDatabase = FirebaseDatabase.getInstance("https://awitize-d10e3-default-rtdb.asia-southeast1.firebasedatabase.app");

    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference music = mDatabase.getReference("music");
        music.push().setValue(new MusicData("artist", "title", "https://drive.google.com/uc?id=1E9alcqJwVotLNx-uzVE7QWSIuFk6WJkI", null, null),
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                        if(error != null){
                            Log.d(TAG, "onComplete: " + "Data could not be saved " + error.getMessage());
                        }
                        else{
                            Log.d(TAG, "onComplete: Successfully Added - " + ref.getKey());
                        }
                    }
                });
    }
}