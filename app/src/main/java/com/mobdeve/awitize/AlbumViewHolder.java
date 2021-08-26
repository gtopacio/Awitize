package com.mobdeve.awitize;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class AlbumViewHolder extends RecyclerView.ViewHolder {

    private TextView tvAlbumName;
    private TextView tvAlbumNumSongs;
    private ConstraintLayout layout;

    public AlbumViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);

        this.tvAlbumName = itemView.findViewById(R.id.tv_category);
        this.tvAlbumNumSongs = itemView.findViewById(R.id.tv_tracks);
        layout = itemView.findViewById(R.id.cl_category);
    }

    public TextView getTvAlbumName () {
        return this.tvAlbumName;
    }

    public TextView getTvAlbumNumSongs () {
        return this.tvAlbumNumSongs;
    }

    public void setTvAlbumName (String albumName) {
        this.tvAlbumName.setText(albumName);
    }

    public void setTvAlbumNumSongs (int albumNumSongs) {
        this.tvAlbumNumSongs.setText(albumNumSongs + " tracks");
    }

    public ConstraintLayout getLayout() {
        return layout;
    }
}
