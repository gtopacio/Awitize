package com.mobdeve.awitize;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistViewHolder> {

    private ArrayList<Artist> artists;

    public ArtistAdapter(ArrayList<Artist> artists) {
        this.artists = artists;
    }

    @NonNull
    @NotNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_category, parent, false);

        ArtistViewHolder artistViewHolder = new ArtistViewHolder(itemView);

        return artistViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ArtistViewHolder holder, int position) {
        holder.setTvArtistName(artists.get(position).getArtistName());
        holder.setTvArtistNumOfSongs(artists.get(position).getArtistNumOfSongs());
    }

    @Override
    public int getItemCount() {
        return this.artists.size();
    }
}
