package com.mobdeve.awitize.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.mobdeve.awitize.MusicData;
import com.mobdeve.awitize.state.GlobalState;

import java.util.LinkedList;

public class PlayerService extends Service {

    public class PlayerBinder extends Binder{
        public PlayerService getService(){
            return PlayerService.this;
        }
    }

    private SimpleExoPlayer player;
    private LinkedList<MusicData> queue;

    private static final String TAG = "PlayerService";

    private BroadcastReceiver playReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!player.isPlaying()){
                player.setPlayWhenReady(true);
                GlobalState.setIsPlaying(player.getPlayWhenReady());
            }
        }
    };

    private BroadcastReceiver pauseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(player.isPlaying()){
                player.setPlayWhenReady(false);
                GlobalState.setIsPlaying(player.getPlayWhenReady());
            }
        }
    };

    public PlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player = new SimpleExoPlayer.Builder(this).build();
        queue = new LinkedList<>();
        player.addListener(new Player.Listener() {

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                GlobalState.setIsPlaying(player.isPlaying());
                Intent i = new Intent(PlayerEvents.NEW_SONG.name());
                LocalBroadcastManager.getInstance(PlayerService.this).sendBroadcast(i);
            }

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {

            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                GlobalState.setIsPlaying(isPlaying);
                Intent i = new Intent(PlayerEvents.STATE_CHANGED.name());
                LocalBroadcastManager.getInstance(PlayerService.this).sendBroadcast(i);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if(playbackState == Player.STATE_ENDED && queue.size() > 0){
                    playSong(queue.pollFirst());
                }
                else if(playbackState == Player.STATE_ENDED){
                    GlobalState.setNowPlaying(null);
                    GlobalState.setIsPlaying(false);
                    Intent i = new Intent(PlayerEvents.NEW_SONG.name());
                    LocalBroadcastManager.getInstance(PlayerService.this).sendBroadcast(i);
                }
                else{
                    GlobalState.setIsPlaying(player.isPlaying());
                    Intent i = new Intent(PlayerEvents.STATE_CHANGED.name());
                    LocalBroadcastManager.getInstance(PlayerService.this).sendBroadcast(i);
                }
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(playReceiver, new IntentFilter(PlayerEvents.PLAY.name()));
        LocalBroadcastManager.getInstance(this).registerReceiver(pauseReceiver, new IntentFilter(PlayerEvents.PAUSE.name()));

        return super.onStartCommand(intent, flags, startId);
    }

    public void queueSong(MusicData musicData){

        if(player.isPlaying() || player.getPlaybackState() == ExoPlayer.STATE_READY){
            Toast.makeText(this, "Queued " + musicData.getArtist() + " - " + musicData.getTitle(), Toast.LENGTH_SHORT).show();
        }

        queue.add(musicData);
        Log.d(TAG, "queueSong: Queued " + musicData.getArtist() + " - " + musicData.getTitle());
        if(player.getPlaybackState() == ExoPlayer.STATE_IDLE && !player.getPlayWhenReady()){
            playSong(queue.pollFirst());
        }
    }

    public void destroySession(){
        player.setPlayWhenReady(false);
        queue.clear();
        GlobalState.setNowPlaying(null);
        GlobalState.setIsPlaying(player.getPlayWhenReady());
    }

    private void playSong(MusicData musicData){
        Log.d(TAG, "playSong: Playing " + musicData.getArtist() + " - " + musicData.getTitle());
        Toast.makeText(this, "Playing " + musicData.getArtist() + " - " + musicData.getTitle(), Toast.LENGTH_SHORT).show();
        GlobalState.setNowPlaying(musicData);
        MediaItem mediaItem = MediaItem.fromUri(musicData.getUrl());
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        IBinder binder = new PlayerBinder();
        return binder;
    }
}