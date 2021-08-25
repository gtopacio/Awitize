package com.mobdeve.awitize.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.mobdeve.awitize.MusicData;

import java.util.LinkedList;

public class PlayerService extends Service {

    public class PlayerBinder extends Binder{
        public PlayerService getService(){
            return PlayerService.this;
        }
    }

    private SimpleExoPlayer player;
    private LinkedList<MusicData> queue;
    private boolean playedOnce;
    private MusicData nowPlaying;

    private static final String TAG = "PlayerService";

    public PlayerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player = new SimpleExoPlayer.Builder(this).build();
        queue = new LinkedList<>();
        playedOnce = false;
        player.addListener(new Player.Listener() {
            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {

            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if(playbackState == Player.STATE_ENDED && queue.size() > 0){
                    playSong(queue.pollFirst());
                }
                else if(playbackState == Player.STATE_IDLE && playedOnce){
                    stopSelf();
                }
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    public void queueSong(MusicData musicData){
        queue.add(musicData);
        if(!playedOnce){
            playedOnce = true;
            playSong(queue.pollFirst());
        }
    }

    public SimpleExoPlayer getPlayer(){
        return player;
    }

    private void playSong(MusicData musicData){
        Log.d(TAG, "playSong: Playing " + musicData.getArtist() + " - " + musicData.getTitle());
        Toast.makeText(this, "Playing " + musicData.getArtist() + " - " + musicData.getTitle(), Toast.LENGTH_SHORT).show();
        MediaItem mediaItem = MediaItem.fromUri(musicData.getUrl());
        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
        nowPlaying = musicData;
    }

    @Override
    public IBinder onBind(Intent intent) {
        IBinder binder = new PlayerBinder();
        return binder;
    }
}