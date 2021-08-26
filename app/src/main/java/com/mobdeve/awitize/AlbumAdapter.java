package com.mobdeve.awitize;

import android.content.Intent;
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

        albumViewHolder.getLayout().setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ShowCategoryActivity.class);
            intent.putExtra(CategoryConstants.CATEGORY_NAME.name(), albums.get(albumViewHolder.getBindingAdapterPosition()).getAlbumName());
            intent.putExtra(CategoryConstants.CATEGORY_TYPE.name(), "ALBUM");
            v.getContext().startActivity(intent);
        });
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
