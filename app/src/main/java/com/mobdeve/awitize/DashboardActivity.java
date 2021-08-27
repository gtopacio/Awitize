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
    private ArrayList<Genre> genres;
    private ArrayList<Artist> artists;
    private ArrayList<Album> albums;
    private RecyclerView rvDasboard;
    private ArtistAdapter artistAdapter;
    private GenreAdapter genreAdapter;
    private AlbumAdapter albumAdapter;
    private FloatingActionButton pageSelect;

    private TextView nowPlayingTitle;
    private TextView nowPlayingArtist;

    private ImageButton accountButton;
    private ImageButton searchButton;
    private ImageButton playPauseButton;
    private ImageButton nextButton;

    private PlayerService playerService;
    private boolean isServiceBounded = false;
    private Spinner spinner;

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

    private BroadcastReceiver albumsUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int pos = spinner.getSelectedItemPosition();
            if(spinner.getItemAtPosition(pos).toString().equals("ALBUM")){
                initRecyclerView("ALBUM");
            }
        }
    };

    private BroadcastReceiver artistsUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int pos = spinner.getSelectedItemPosition();
            if(spinner.getItemAtPosition(pos).toString().equals("ARTIST")){
                initRecyclerView("ARTIST");
            }
        }
    };

    private BroadcastReceiver genresUpdatedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int pos = spinner.getSelectedItemPosition();
            if(spinner.getItemAtPosition(pos).toString().equals("GENRE")){
                initRecyclerView("GENRE");
            }
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

        loadComponents();

        // FOR TESTING ONLY
        spinner = findViewById(R.id.sp_category_select);
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
                String categoryName = parent.getItemAtPosition(position).toString();
                //Toast.makeText(parent.getContext(), "Selected: " + categoryName, Toast.LENGTH_LONG).show();
                initRecyclerView(categoryName);
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });
    }

    private void loadComponents(){
        nowPlayingTitle = findViewById(R.id.tv_now_playing_main_title);
        nowPlayingArtist = findViewById(R.id.tv_now_playing_main_artist);
        pageSelect = findViewById(R.id.fab_page_select_main);
        accountButton = findViewById(R.id.ib_account_main);
        searchButton = findViewById(R.id.ib_search_main);
        playPauseButton = findViewById(R.id.ib_play_main);
        nextButton = findViewById(R.id.ib_next_main);

        nowPlayingTitle.setOnClickListener(v -> {
            Intent i = new Intent(DashboardActivity.this, MusicPlayerActivity.class);
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
            Intent i = new Intent(this, SearchActivity.class);
            startActivity(i);
        });

        nextButton.setOnClickListener(v -> {
            Intent i = new Intent(PlayerEvents.SKIP.name());
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
        LocalBroadcastManager.getInstance(this).registerReceiver(albumsUpdatedReceiver, new IntentFilter("Albums Updated"));
        LocalBroadcastManager.getInstance(this).registerReceiver(artistsUpdatedReceiver, new IntentFilter("Artist Updated"));
        LocalBroadcastManager.getInstance(this).registerReceiver(genresUpdatedReceiver, new IntentFilter("Genres Updated"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newSongReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(playerStateChanged);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(albumsUpdatedReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(artistsUpdatedReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(genresUpdatedReceiver);
        unbindService(connection);
    }

    private void initRecyclerView(String selected) {
        switch (selected) {
            case "ARTIST":
                this.artists = GlobalState.getArtists() == null ? new ArrayList<>() : GlobalState.getArtists();
                this.rvDasboard = findViewById(R.id.rv_category_selection);
                this.rvDasboard.setAlpha(1);
                this.rvDasboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                this.artistAdapter = new ArtistAdapter(this.artists);
                this.rvDasboard.setAdapter(this.artistAdapter);
                break;
            case "ALBUM":
                this.albums = GlobalState.getAlbums() == null ? new ArrayList<>() : GlobalState.getAlbums();
                this.rvDasboard = findViewById(R.id.rv_category_selection);
                this.rvDasboard.setAlpha(1);
                this.rvDasboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                this.albumAdapter = new AlbumAdapter(this.albums);
                this.rvDasboard.setAdapter(this.albumAdapter);
                break;
            default:
                this.genres = GlobalState.getGenres() == null ? new ArrayList<>() : GlobalState.getGenres();
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
            nowPlayingTitle.setText(curr.getTitle());
            nowPlayingArtist.setText(curr.getArtist());
            int image = GlobalState.isIsPlaying() ? R.drawable.ic___70_pause_button : R.drawable.ic___72_play_button;
            playPauseButton.setBackgroundResource(image);
        }
        else{
            nowPlayingTitle.setText("No Song");
            nowPlayingArtist.setText("No Artist");
            int image = GlobalState.isIsPlaying() ? R.drawable.ic___70_pause_button : R.drawable.ic___72_play_button;
            playPauseButton.setBackgroundResource(image);
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