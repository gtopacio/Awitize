package com.mobdeve.awitize;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SearchSongAdapter extends RecyclerView.Adapter<SearchSongViewHolder> {

    private ArrayList<MusicData> searchedSongs;

    public SearchSongAdapter(ArrayList<MusicData> songs) {
        this.searchedSongs = songs;
    }

    @NonNull
    @NotNull
    @Override
    public SearchSongViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_song, parent, false);

        SearchSongViewHolder searchSongViewHolder = new SearchSongViewHolder(itemView);

        return searchSongViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchSongViewHolder holder, int position) {
        holder.setTvSeachedArtistTvArtist(searchedSongs.get(position).getArtist());
        holder.setTvSearchedSongTvSong(searchedSongs.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return this.searchedSongs.size();
    }
}
