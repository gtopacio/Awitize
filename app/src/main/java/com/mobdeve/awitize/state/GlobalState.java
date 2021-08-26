package com.mobdeve.awitize.state;

import com.mobdeve.awitize.MusicData;

public class GlobalState {

    private static MusicData nowPlaying;
    private static boolean isPlaying = false;

    public static void setNowPlaying(MusicData musicData){
        nowPlaying = musicData;
    }

    public static MusicData getNowPlaying(){
        return nowPlaying;
    }

    public static boolean isIsPlaying() {
        return isPlaying;
    }

    public static void setIsPlaying(boolean isPlaying) {
        GlobalState.isPlaying = isPlaying;
    }
}
