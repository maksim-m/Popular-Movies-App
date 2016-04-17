package me.maxdev.popularmoviesapp.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.api.DiscoverResponse;
import me.maxdev.popularmoviesapp.api.TheMovieDbClient;
import me.maxdev.popularmoviesapp.api.TheMovieDbService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesService extends IntentService {

    private static final String SERVICE_NAME = "MoviesService";
    private static final String LOG_TAG = "MoviesService";

    public MoviesService() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        updateMovies();
    }

    private void updateMovies() {
        TheMovieDbService service = TheMovieDbClient.getTheMovieDbService(this);

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

                    ContentValues[] values = new ContentValues[movies.size()];
                    for (int i = 0; i < movies.size(); i++) {
                        values[i] = movies.get(i).toContentValues();
                    }
                    getContentResolver().bulkInsert(
                            MoviesContract.MovieEntry.CONTENT_URI, values);
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getString(
                getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default)
        );
    }

}
