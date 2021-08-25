package com.mobdeve.awitize;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class ArtistViewHolder extends RecyclerView.ViewHolder {

    private TextView tvArtistName;
    private TextView tvArtistNumOfSongs;

    public ArtistViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.tvArtistName = itemView.findViewById(R.id.tv_category);
        this.tvArtistNumOfSongs = itemView.findViewById(R.id.tv_tracks);
    }

    public TextView getTvArtistName () {
        return this.tvArtistName;
    }

    public TextView getTvArtistNumOfSongs () {
        return this.tvArtistNumOfSongs;
    }

    public void setTvArtistName (String artistName) {
        this.tvArtistName.setText(artistName);
    }

    public void setTvArtistNumOfSongs (int songs) {
        this.tvArtistNumOfSongs.setText(songs + " tracks");
    }

}
