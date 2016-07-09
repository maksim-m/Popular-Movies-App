package me.maxdev.popularmoviesapp.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.data.Movie;
import me.maxdev.popularmoviesapp.ui.movies.FavoritesGridFragment;
import me.maxdev.popularmoviesapp.ui.movies.MoviesGridFragment;
import me.maxdev.popularmoviesapp.ui.movies.detail.MovieDetailActivity;
import me.maxdev.popularmoviesapp.ui.movies.detail.MovieDetailFragment;
import me.maxdev.popularmoviesapp.util.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String SELECTED_MOVIE_KEY = "MovieSelected";
    private static final String SELECTED_NAVIGATION_ITEM_KEY = "SelectedNavigationItem";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Nullable
    @BindView(R.id.movie_detail_container)
    FrameLayout movieDetailContainer;

    private boolean twoPaneMode;
    private boolean movieSelected;
    private int selectedNavigationItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movies_grid_container, MoviesGridFragment.create())
                    .commit();
        }
        twoPaneMode = findViewById(R.id.movie_detail_container) != null;
        if (twoPaneMode && !movieSelected && movieDetailContainer != null) {
            movieDetailContainer.setVisibility(View.GONE);
        }
        setupToolbar();
        setupNavigationDrawer();
        setupNavigationView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SELECTED_MOVIE_KEY, movieSelected);
        outState.putInt(SELECTED_NAVIGATION_ITEM_KEY, selectedNavigationItem);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            movieSelected = savedInstanceState.getBoolean(SELECTED_MOVIE_KEY);
            selectedNavigationItem = savedInstanceState.getInt(SELECTED_NAVIGATION_ITEM_KEY);
            Menu menu = navigationView.getMenu();
            menu.getItem(selectedNavigationItem).setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Movie movie) {
        if (twoPaneMode && movieDetailContainer != null) {
            movieDetailContainer.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, MovieDetailFragment.create(movie))
                    .commit();
        } else {
            MovieDetailActivity.start(this, movie);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.drawer_item_explore:
                if (selectedNavigationItem != 0) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movies_grid_container, MoviesGridFragment.create())
                            .commit();
                    selectedNavigationItem = 0;
                    hideMovieDetailContainer();
                }
                drawerLayout.closeDrawers();
                return true;
            case R.id.drawer_item_favorites:
                if (selectedNavigationItem != 1) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movies_grid_container, FavoritesGridFragment.create())
                            .commit();
                    selectedNavigationItem = 1;
                    hideMovieDetailContainer();
                }
                drawerLayout.closeDrawers();
                return true;
            default:
                return false;
        }
    }

    private void hideMovieDetailContainer() {
        if (twoPaneMode && movieDetailContainer != null) {
            movieDetailContainer.setVisibility(View.GONE);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupNavigationDrawer() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
    }
}
