package com.mobdeve.awitize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mobdeve.awitize.services.PlayerEvents;
import com.mobdeve.awitize.services.PlayerService;
import com.mobdeve.awitize.state.GlobalState;

import java.util.ArrayList;
import java.util.Iterator;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseUser user;
    private TextView emailView;
    private ArrayList<MusicData> songs;
    private ArrayList<Genre> genres;
    private ArrayList<Artist> artists;
    private ArrayList<Album> albums;
    private RecyclerView rvDasboard;
    private ArtistAdapter artistAdapter;
    private GenreAdapter genreAdapter;
    private AlbumAdapter albumAdapter;
    private FloatingActionButton pageSelect;

    private TextView nowPlaying;

    private ImageButton accountButton;
    private ImageButton searchButton;
    private ImageButton playPauseButton;

    private PlayerService playerService;
    private boolean isServiceBounded = false;

    private static final String TAG = "DashboardActivity";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSession();

        Intent serviceIntent = new Intent(this, PlayerService.class);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(newSongReceiver, new IntentFilter(PlayerEvents.NEW_SONG.name()));
        LocalBroadcastManager.getInstance(this).registerReceiver(playerStateChanged, new IntentFilter(PlayerEvents.STATE_CHANGED.name()));

        loadSongs();
        loadComponents();

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
                //Toast.makeText(parent.getContext(), "Selected: " + tutorialsName, Toast.LENGTH_LONG).show();
                initRecyclerView(tutorialsName);
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });
    }

    private void loadComponents(){
        nowPlaying = findViewById(R.id.tv_now_playing_main);
        pageSelect = findViewById(R.id.fab_page_select_main);
        accountButton = findViewById(R.id.ib_account_main);
        searchButton = findViewById(R.id.ib_search_main);
        playPauseButton = findViewById(R.id.ib_play_main);

        nowPlaying.setOnClickListener(v -> {
            Intent i = new Intent(DashboardActivity.this, MusicPlayerActivity.class);
            MusicData song = songs.get(0);
            i.putExtra(SongAttributes.TITLE.name(), song.getTitle());
            i.putExtra(SongAttributes.ARTIST.name(), song.getArtist());
            i.putExtra(SongAttributes.URL.name(), song.getUrl());
            i.putExtra(IntentKeys.PREVIOUS_CLASS.name(), this.getClass().getName());
            startActivity(i);
            finish();
        });

        pageSelect.setOnClickListener(v -> {
            Intent i = new Intent(DashboardActivity.this, MyLibraryActivity.class);
            startActivity(i);
            finish();
        });

        accountButton.setOnClickListener(v -> {
            Intent i = new Intent(DashboardActivity.this, AccountActivity.class);
            i.putExtra(IntentKeys.PREVIOUS_CLASS.name(), this.getClass().getName());
            startActivity(i);
            finish();
        });

        playPauseButton.setOnClickListener(v -> {
            if(GlobalState.isIsPlaying()){
                Intent i = new Intent(PlayerEvents.PAUSE.name());
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            }
            else{
                Intent i = new Intent(PlayerEvents.PLAY.name());
                LocalBroadcastManager.getInstance(this).sendBroadcast(i);
            }
        });

        searchButton.setOnClickListener(v -> {
            //Intent for search activity
        });
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
    }

    private void loadSongs() {
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
                    while(retSongs.hasNext()) {
                        DataSnapshot d = retSongs.next();
                        String artist = String.valueOf(d.child("artist").getValue());
                        String title = String.valueOf(d.child("title").getValue());
                        String url = String.valueOf(d.child("url").getValue());
                        String genre = String.valueOf(d.child("genre").getValue());
                        String album = String.valueOf(d.child("album").getValue());
                        Log.w("Loaded", artist + " - " + title + ", " + genre + " " + album + " " + url);
                        MusicData songData = new MusicData(artist, title, url, genre, album);
                        songs.add(songData);
                        playerService.queueSong(songData);
                    }
                }
            }
        });
    }

    private void initRecyclerView(String selected) {
        switch (selected) {
            case "ARTIST":
                this.artists = ArtistDataHelper.loadArtist();
                this.rvDasboard = findViewById(R.id.rv_category_selection);
                this.rvDasboard.setAlpha(1);
                this.rvDasboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                this.artistAdapter = new ArtistAdapter(this.artists);
                this.rvDasboard.setAdapter(this.artistAdapter);
                break;
            case "ALBUM":
                this.albums = AlbumDataHelper.loadAlbums();
                this.rvDasboard = findViewById(R.id.rv_category_selection);
                this.rvDasboard.setAlpha(1);
                this.rvDasboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                this.albumAdapter = new AlbumAdapter(this.albums);
                this.rvDasboard.setAdapter(this.albumAdapter);
                break;
            default:
                this.genres = GenreDataHelper.loadGenres();
                this.rvDasboard = findViewById(R.id.rv_category_selection);
                this.rvDasboard.setAlpha(1);
                this.rvDasboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                this.genreAdapter = new GenreAdapter(this.genres);
                this.rvDasboard.setAdapter(this.genreAdapter);
        }
    }

    private void updateUI(){
        MusicData curr = GlobalState.getNowPlaying();
        if(curr != null){
            nowPlaying.setText(curr.getArtist() + " - " + curr.getTitle());
            int image = GlobalState.isIsPlaying() ? R.drawable.exo_controls_pause : R.drawable.exo_controls_play;
            playPauseButton.setImageResource(image);
        }
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