package me.maxdev.popularmoviesapp.ui.movies;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.data.Movie;
import me.maxdev.popularmoviesapp.util.CursorRecyclerViewAdapter;

public class MoviesAdapter extends CursorRecyclerViewAdapter<MovieGridItemViewHolder> {

    private static String POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static String POSTER_IMAGE_SIZE = "w342";
    private Context context;

    public MoviesAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(MovieGridItemViewHolder viewHolder, Cursor cursor) {
        Movie movie = Movie.fromCursor(cursor);
        Picasso.with(context)
                .load(POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + movie.getPosterPath())
                .into(viewHolder.moviePoster);
    }

    @Override
    public MovieGridItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_movie, parent, false);
        return new MovieGridItemViewHolder(itemView);
    }

}
