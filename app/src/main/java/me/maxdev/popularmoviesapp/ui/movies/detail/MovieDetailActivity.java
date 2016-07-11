package me.maxdev.popularmoviesapp.ui.movies.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.data.FavoritesService;
import me.maxdev.popularmoviesapp.data.Movie;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String ARG_MOVIE = "argMovie";
    private static final String POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String BACKDROP_IMAGE_SIZE = "w780";

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.backdrop_image)
    ImageView movieBackdropImage;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private FavoritesService favoritesService;
    private Movie movie;

    public static void start(Context context, Movie movie) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(ARG_MOVIE, movie);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        movie = getIntent().getParcelableExtra(ARG_MOVIE);
        favoritesService = FavoritesService.getInstance(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movies_grid_container, MovieDetailFragment.create(movie))
                    .commit();
        }
        initToolbar();
        updateFab();
        setTitle(movie.getTitle());
    }

    @OnClick(R.id.fab)
    void onFabClicked() {
        if (favoritesService.isFavorite(movie)) {
            favoritesService.removeFromFavorites(movie);
            showSnackbar(R.string.message_removed_from_favorites);
        } else {
            favoritesService.addToFavorites(movie);
            showSnackbar(R.string.message_added_to_favorites);
        }

        updateFab();
    }


    private void updateFab() {
        if (favoritesService.isFavorite(movie)) {
            fab.setImageResource(R.drawable.ic_favorite_white);
        } else {
            fab.setImageResource(R.drawable.ic_favorite_white_border);
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(view -> onBackPressed());
        }
        Glide.with(this)
                .load(POSTER_IMAGE_BASE_URL + BACKDROP_IMAGE_SIZE + movie.getBackdropPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .crossFade()
                .into(movieBackdropImage);
    }

    private void showSnackbar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void showSnackbar(@StringRes int messageResourceId) {
        showSnackbar(getString(messageResourceId));
    }

}
