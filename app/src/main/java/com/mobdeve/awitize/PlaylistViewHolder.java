package com.mobdeve.awitize;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class PlaylistViewHolder extends RecyclerView.ViewHolder{

    private ConstraintLayout clPlaylist;
    private TextView tvPlaylistName;
    private TextView tvNumOfSongsPL;

    public PlaylistViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.clPlaylist = itemView.findViewById(R.id.cl_playlist);
        this.tvPlaylistName = itemView.findViewById(R.id.tv_playlist_name);
        this.tvNumOfSongsPL = itemView.findViewById(R.id.tv_num_of_songs_playlist);
    }

    public ConstraintLayout getClPlaylist() { return this.clPlaylist; }

    public TextView getTvPlaylistName () {
        return this.tvPlaylistName;
    }

    public TextView getTvNumOfSongsPL () {
        return this.tvNumOfSongsPL;
    }

    public void setTvPlaylistName (String playlistName) {
        this.tvPlaylistName.setText(playlistName);
    }

    public void setTvNumOfSongsPL (int numOfSongsPL) {
        this.tvNumOfSongsPL.setText(numOfSongsPL + " Songs");
    }
}
