package me.maxdev.popularmoviesapp.ui.movies.detail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.data.Movie;
import me.maxdev.popularmoviesapp.data.MoviesContract;

public class MovieDetailsActivity extends AppCompatActivity {

    private static String POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static String POSTER_IMAGE_SIZE = "w780";
    private static String BACKDROP_IMAGE_SIZE = "w780";

    private ImageView movieImagePoster;
    private ImageView movieBackdropImage;
    private TextView movieOriginalTitle;
    private TextView movieUserRating;
    private TextView movieReleaseDate;
    private TextView movieOverview;
    private Toolbar toolbar;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        movieImagePoster = (ImageView) findViewById(R.id.image_movie_detail_poster);
        movieOriginalTitle = (TextView) findViewById(R.id.text_movie_original_title);
        movieUserRating = (TextView) findViewById(R.id.text_movie_user_rating);
        movieReleaseDate = (TextView) findViewById(R.id.text_movie_release_date);
        movieOverview = (TextView) findViewById(R.id.text_movie_overview);
        movieBackdropImage = (ImageView) findViewById(R.id.backdrop_image);
        initToolbar();
        initMovie();
        initViews();
    }

    private void initMovie() {
        long movieId = getIntent().getLongExtra("movieId", 0);
        Cursor cursor = getContentResolver().query(
                MoviesContract.MovieEntry.buildMovieUri(movieId),
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        movie = Movie.fromCursor(cursor);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initViews() {
        setTitle(movie.getTitle());
        Picasso.with(this)
                .load(POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + movie.getPosterPath())
                .into(movieImagePoster);
        Picasso.with(this)
                .load(POSTER_IMAGE_BASE_URL + BACKDROP_IMAGE_SIZE + movie.getBackdropPath())
                .into(movieBackdropImage);
        movieOriginalTitle.setText(movie.getOriginalTitle());
        String userRating = String.format(getString(me.maxdev.popularmoviesapp.R.string.user_rating), movie.getAverageVote());
        movieUserRating.setText(userRating);
        String releaseDate = String.format(getString(me.maxdev.popularmoviesapp.R.string.release_date), movie.getReleaseDate());
        movieReleaseDate.setText(releaseDate);
        movieOverview.setText(movie.getOverview());
    }

}
