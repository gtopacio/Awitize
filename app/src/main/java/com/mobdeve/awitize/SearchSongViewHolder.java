package com.mobdeve.awitize;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class SearchSongViewHolder extends RecyclerView.ViewHolder {

    private TextView tvSeachedArtist;
    private TextView tvSearchedSong;
    private ConstraintLayout searchLayout;

    public SearchSongViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.tvSeachedArtist = itemView.findViewById(R.id.tv_artist);
        this.tvSearchedSong = itemView.findViewById(R.id.tv_song);
        this.searchLayout = itemView.findViewById(R.id.cl_song);
    }

    public TextView getTvSearchedSongTvArtist () {return this.tvSeachedArtist;}
    public TextView getTvSearchedSongSong () {return this.tvSearchedSong;}

    public void setTvSeachedArtistTvArtist (String artistName) {
        this.tvSeachedArtist.setText(artistName);
    }

    public void setTvSearchedSongTvSong (String songName) {
        this.tvSearchedSong.setText(songName);
    }

    public ConstraintLayout getSearchLayout() {
        return searchLayout;
    }
}