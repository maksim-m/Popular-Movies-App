package me.maxdev.popularmoviesapp.ui.movies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import me.maxdev.popularmoviesapp.R;

public class MovieGridItemViewHolder extends RecyclerView.ViewHolder {

    public ImageView moviePoster;

    public MovieGridItemViewHolder(View itemView) {
        super(itemView);
        moviePoster = (ImageView) itemView.findViewById(R.id.image_movie_poster);
    }
}
