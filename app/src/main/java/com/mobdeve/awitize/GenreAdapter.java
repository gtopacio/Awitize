package com.mobdeve.awitize;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class GenreAdapter extends RecyclerView.Adapter<GenreViewHolder> {

    private ArrayList<Genre> genres;

    public GenreAdapter(ArrayList<Genre> genres) {
        this.genres = genres;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.item_category, parent, false);

        GenreViewHolder genreviewholder = new GenreViewHolder(itemView);

        genreviewholder.getClGenre().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext().)
            }
        });

        return genreviewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull GenreViewHolder holder, int position) {
        holder.setTvGenreName(genres.get(position).getGenre());
        holder.setTvNumOfSongs(genres.get(position).getNumOfSongs());
    }

    @Override
    public int getItemCount() {
        return this.genres.size();
    }
}
