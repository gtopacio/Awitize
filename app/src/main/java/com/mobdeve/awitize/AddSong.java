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
        /*
        // Fireworks
        music.push().setValue(new MusicData("Noiseless-World", "Fireworks", "https://drive.google.com/uc?id=1TZy6fNL8nId1X0EqhRfDFVuCT0EjtNuy", null, null),
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

        // Ghost Girl
        music.push().setValue(new MusicData("Noiseless-World", "Ghost Girl", "https://drive.google.com/uc?id=1CpjBQ81z1IN39YABoJjdAkMCCrfQ1Ken", null, null),
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

        // Good Morning
        music.push().setValue(new MusicData("Noiseless-World", "Good Morning", "https://drive.google.com/uc?id=1w7OS3k6myxuDKOmXOc0IfbwHNoJavf4N", null, null),
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

        // Happy Spring
        music.push().setValue(new MusicData("Noiseless-World", "Happy Spring", "https://drive.google.com/uc?id=1kObxKlEDpdwL1uiI0OtVn-XkTFhjkhdu", null, null),
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

        // Jet Penguin
        music.push().setValue(new MusicData("Noiseless-World", "Jet Penguin", "https://drive.google.com/uc?id=1-D-eRQmLeIXdZcO-TvNFzLTWqP5TJW_z", null, null),
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

        // Lantana
        music.push().setValue(new MusicData("Noiseless-World", "Lantana", "https://drive.google.com/uc?id=1kXjwTL7YKu8RHmVsdgh-xAuZbs4bHq6q", null, null),
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

        // My Hero
        music.push().setValue(new MusicData("Noiseless-World", "My Hero", "https://drive.google.com/uc?id=1PT9dkJeE33PabgKye2CrZ3sbMdrYeDws", null, null),
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

        // Naked King
        music.push().setValue(new MusicData("Noiseless-World", "Naked King", "https://drive.google.com/uc?id=1aFC8noRnU_ivFHLeNQLGx_AEOuG7GU97", null, null),
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

        // Never-Ending Story
        music.push().setValue(new MusicData("Noiseless-World", "Never-Ending Story", "https://drive.google.com/uc?id=19YL6R5-rz9ZnxDdbaxPmhbB8bLWsNjE_", null, null),
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

        // Rain in the Desert
        music.push().setValue(new MusicData("Noiseless-World", "Rain in the Desert", "https://drive.google.com/uc?id=18ibXQhwRSGj0ZOWfAUCaEj_p7-F8I3sm", null, null),
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

        // Transparent Days
        music.push().setValue(new MusicData("Noiseless-World", "Transparent Days", "https://drive.google.com/uc?id=1l_eDpHYCgxk2K5JYJAsFE64qw4Pq98rK", null, null),
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

         */
    }
}