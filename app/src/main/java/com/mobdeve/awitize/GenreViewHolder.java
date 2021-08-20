package com.mobdeve.awitize;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class GenreViewHolder extends RecyclerView.ViewHolder {

    private ConstraintLayout clGenre;
    private TextView tvGenreName;
    private TextView tvNumOfSongs;

    public GenreViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
        super(itemView);

        this.clGenre = itemView.findViewById(R.id.cl_genre);
        this.tvNumOfSongs = itemView.findViewById(R.id.tv_genre);
        this.tvNumOfSongs = itemView.findViewById(R.id.tv_tracks);

    }

    public ConstraintLayout getClGenre () {
        return this.clGenre;
    }

    public TextView getGenreName () {
        return this.tvNumOfSongs;
    }

    public TextView getNumOfSongs () {
        return this.tvNumOfSongs;
    }

    public void setTvGenreName (String genre) {
        this.tvGenreName.setText(genre);
    }

    public void setTvNumOfSongs (int songs) {
        this.tvNumOfSongs.setText(songs);
    }

}
