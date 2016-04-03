package me.maxdev.popularmoviesapp.ui;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.data.Movie;
import com.squareup.picasso.Picasso;

public class MoviesAdapter extends ArrayAdapter<Movie> {

    private static String POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static String POSTER_IMAGE_SIZE = "w342";
    private Context context;
    private int layoutResId;
    private LayoutInflater layoutInflater;

    public MoviesAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        this.layoutResId = resource;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    static class MovieHolder {
        ImageView moviePoster;
        //TextView movieTitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item_movie, null);
            holder = new MovieHolder();
            holder.moviePoster = (ImageView) convertView.findViewById(R.id.image_movie_poster);
            //holder.movieTitle = (TextView) convertView.findViewById(R.id.text_movie_title);
            convertView.setTag(holder);
        } else {
            holder = (MovieHolder) convertView.getTag();
        }
        Movie movie = getItem(position);

        //holder.movieTitle.setText(movie.getTitle());
        Picasso.with(context)
                .load(POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + movie.getPosterPath())
                .into(holder.moviePoster);

        return convertView;
    }
}
