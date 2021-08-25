package com.mobdeve.awitize;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistViewHolder>{

    private ArrayList<Playlist> playlists;

    public PlaylistAdapter(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
    }


    @NonNull
    @NotNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_playlist, parent,false);

        PlaylistViewHolder playlistViewHolder = new PlaylistViewHolder(itemView);

        return playlistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PlaylistViewHolder holder, int position) {
        holder.setTvPlaylistName(playlists.get(position).getPlaylistName());
        holder.setTvNumOfSongsPL(playlists.get(position).getNumOfSongs());
    }

    @Override
    public int getItemCount() {
        return this.playlists.size();
    }
}
