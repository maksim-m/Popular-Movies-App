package me.maxdev.popularmoviesapp.ui.movies.detail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.data.Movie;
import me.maxdev.popularmoviesapp.data.MoviesContract;

public class MovieDetailsActivity extends AppCompatActivity {

    private static final String POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String POSTER_IMAGE_SIZE = "w780";
    private static final String BACKDROP_IMAGE_SIZE = "w780";

    @BindView(R.id.image_movie_detail_poster)
    ImageView movieImagePoster;
    @BindView(R.id.backdrop_image)
    ImageView movieBackdropImage;
    @BindView(R.id.text_movie_original_title)
    TextView movieOriginalTitle;
    @BindView(R.id.text_movie_user_rating)
    TextView movieUserRating;
    @BindView(R.id.text_movie_release_date)
    TextView movieReleaseDate;
    @BindView(R.id.text_movie_overview)
    TextView movieOverview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);
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
        if (cursor != null) {
            cursor.moveToFirst();
            movie = Movie.fromCursor(cursor);
            cursor.close();
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    @OnClick(R.id.fab)
    void onFabClicked(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void initViews() {
        setTitle(movie.getTitle());
        Glide.with(this)
                .load(POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + movie.getPosterPath())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(movieImagePoster);
        Glide.with(this)
                .load(POSTER_IMAGE_BASE_URL + BACKDROP_IMAGE_SIZE + movie.getBackdropPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(movieBackdropImage);
        movieOriginalTitle.setText(movie.getOriginalTitle());
        String userRating = String.format(getString(me.maxdev.popularmoviesapp.R.string.user_rating),
                movie.getAverageVote());
        movieUserRating.setText(userRating);
        String releaseDate = String.format(getString(me.maxdev.popularmoviesapp.R.string.release_date),
                movie.getReleaseDate());
        movieReleaseDate.setText(releaseDate);
        movieOverview.setText(movie.getOverview());
    }

}
