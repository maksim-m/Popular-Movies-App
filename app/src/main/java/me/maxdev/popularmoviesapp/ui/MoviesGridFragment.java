package me.maxdev.popularmoviesapp.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.api.TheMovieDbClient;
import me.maxdev.popularmoviesapp.api.TheMovieDbService;
import me.maxdev.popularmoviesapp.model.DiscoverResponse;
import me.maxdev.popularmoviesapp.model.Movie;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MoviesGridFragment extends Fragment {

    private static final String LOG_TAG = "MoviesGridFragment";
    private MoviesAdapter adapter;

    public MoviesGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMovies();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                updateMovies();
                return true;
            case R.id.action_show_sort_by_dialog:
                showSortByDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSortByDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogStyle);
        builder.setTitle(getString(R.string.pref_sort_by_label));
        builder.setNegativeButton("Cancel", null);
        builder.setSingleChoiceItems(R.array.pref_sort_by_labels, getSortByPreferenceIndex(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveSortByPreference(which);
                updateMovies();
                dialog.dismiss();
            }
        });
        final AlertDialog sortByDialog = builder.create();
        sortByDialog.show();
    }



    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        adapter = new MoviesAdapter(getActivity(), 0);


        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = adapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra("movieTitle", movie.getTitle());
                intent.putExtra("movieOriginalTitle", movie.getOriginalTitle());
                intent.putExtra("moviePosterPath", movie.getPosterPath());
                intent.putExtra("movieUserRating", movie.getAverageVote());
                intent.putExtra("movieReleaseDate", movie.getReleaseDate());
                intent.putExtra("movieOverview", movie.getOverview());
                intent.putExtra("movieBackdropPath", movie.getBackdropPath());
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviesgridfragment, menu);
    }

    private void updateMovies() {
        TheMovieDbService service = TheMovieDbClient.getTheMovieDbService(getContext());

        Call<DiscoverResponse<Movie>> call = service.discoverMovies(getSortByPreference());

        call.enqueue(new Callback<DiscoverResponse<Movie>>() {
            @Override
            public void onResponse(Call<DiscoverResponse<Movie>> call, Response<DiscoverResponse<Movie>> response) {
                if (response == null) {
                    Log.d(LOG_TAG, "Response is null!");
                    return;
                }
                if (!response.isSuccessful()) {
                    Log.d(LOG_TAG, "error: " + response.message());
                } else {
                    Log.d(LOG_TAG, "Successful!");
                    Log.d(LOG_TAG, response.message());
                    DiscoverResponse<Movie> discoverResponse = response.body();
                    List<Movie> movies = discoverResponse.getResults();
                    Log.d(LOG_TAG, movies.toString());
                    adapter.clear();
                    adapter.addAll(movies);
                }
            }

            @Override
            public void onFailure(Call<DiscoverResponse<Movie>> call, Throwable t) {
                Log.d(LOG_TAG, "Error!");
                Log.d(LOG_TAG, t.getMessage());
            }
        });

    }

    private String getSortByPreference() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        return prefs.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default)
        );
    }

    private int getSortByPreferenceIndex() {
        String sortByPreference = getSortByPreference();
        List<String> sortByOptionsLabels = Arrays.asList(getResources().getStringArray(R.array.pref_sort_by_values));
        return sortByOptionsLabels.indexOf(sortByPreference);
    }

    private void saveSortByPreference(int index) {
        String[] sortByPreferencesValues = getResources().getStringArray(R.array.pref_sort_by_values);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString(
                getString(me.maxdev.popularmoviesapp.R.string.pref_sort_by_key),
                sortByPreferencesValues[index]
        );
        editor.commit();
    }
}
