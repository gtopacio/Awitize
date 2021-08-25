package com.mobdeve.awitize;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumViewHolder> {

    private ArrayList<Album> albums;

    public AlbumAdapter(ArrayList<Album> albums) {
        this.albums = albums;
    }

    @NonNull
    @NotNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_category, parent, false);

        AlbumViewHolder albumViewHolder = new AlbumViewHolder(itemView);

        return albumViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AlbumViewHolder holder, int position) {
        holder.setTvAlbumName(albums.get(position).getAlbumName());
        holder.setTvAlbumNumSongs(albums.get(position).getAlbumNumOfSongs());
    }

    @Override
    public int getItemCount() {
        return this.albums.size();
    }
}
