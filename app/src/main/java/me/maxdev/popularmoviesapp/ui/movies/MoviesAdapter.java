package me.maxdev.popularmoviesapp.ui.movies;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.data.Movie;
import me.maxdev.popularmoviesapp.util.CursorRecyclerViewAdapter;
import me.maxdev.popularmoviesapp.util.OnItemClickListener;

public class MoviesAdapter extends CursorRecyclerViewAdapter<MovieGridItemViewHolder> {

    private static String POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static String POSTER_IMAGE_SIZE = "w780";
    private Context context;
    private OnItemClickListener onItemClickListener;

    public MoviesAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @SuppressLint("PrivateResource")
    @Override
    public void onBindViewHolder(MovieGridItemViewHolder viewHolder, Cursor cursor) {
        if (cursor != null) {
            Movie movie = Movie.fromCursor(cursor);
            viewHolder.moviePoster.setContentDescription(movie.getTitle());
            Glide.with(context)
                    .load(POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + movie.getPosterPath())
                    .placeholder(new ColorDrawable(context.getResources().getColor(R.color.accent_material_light)))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .crossFade()
                    .into(viewHolder.moviePoster);
        }

    }

    @Override
    public MovieGridItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_movie, parent, false);
        return new MovieGridItemViewHolder(itemView, onItemClickListener);
    }

}
