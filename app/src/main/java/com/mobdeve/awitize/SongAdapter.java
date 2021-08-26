package com.mobdeve.awitize;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongViewHolder> {

    private ArrayList<MusicData> songs;

    public SongAdapter(ArrayList<MusicData> songs) {this.songs = songs;}

    @NonNull
    @NotNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_song, parent, false);

        SongViewHolder songViewHolder = new SongViewHolder(itemView);

        return songViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SongViewHolder holder, int position) {
        holder.setTvArtist(songs.get(position).getArtist());
        holder.setTvSong(songs.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return this.songs.size();
    }
}
