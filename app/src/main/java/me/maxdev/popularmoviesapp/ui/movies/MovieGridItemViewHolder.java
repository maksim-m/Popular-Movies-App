package me.maxdev.popularmoviesapp.ui.movies;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.util.OnItemClickListener;

public class MovieGridItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView moviePoster;
    private OnItemClickListener onItemClickListener;

    public MovieGridItemViewHolder(View itemView, @Nullable OnItemClickListener onItemClickListener) {
        super(itemView);
        moviePoster = (ImageView) itemView.findViewById(R.id.image_movie_poster);
        this.onItemClickListener = onItemClickListener;
        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }
}
