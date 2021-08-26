package com.mobdeve.awitize;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

public class SongViewHolder extends RecyclerView.ViewHolder {

    private TextView tvArtist;
    private TextView tvSong;

    public SongViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.tvArtist = itemView.findViewById(R.id.tv_artist);
        this.tvSong = itemView.findViewById(R.id.tv_song);
    }

    public TextView getTvArtist () {return this.tvArtist;}
    public TextView getTvSong () {return this.tvSong;}

    public void setTvArtist (String artistName) {
        this.tvArtist.setText(artistName);
    }

    public void setTvSong (String songName) {
        this.tvSong.setText(songName);
    }
}