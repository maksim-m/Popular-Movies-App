package me.maxdev.popularmoviesapp.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;

import me.maxdev.popularmoviesapp.api.DiscoverResponse;
import me.maxdev.popularmoviesapp.api.TheMovieDbClient;
import me.maxdev.popularmoviesapp.api.TheMovieDbService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesService extends IntentService implements Callback<DiscoverResponse<Movie>> {

    public static final String BROADCAST_UPDATE_FINISHED = "UpdateFinished";

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

        Call<DiscoverResponse<Movie>> call = service.discoverMovies(
                PreferencesUtility.getSortByPreference(this));

        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<DiscoverResponse<Movie>> call, Response<DiscoverResponse<Movie>> response) {
        new SaveMoviesToDbTask().execute(response);
    }

    @Override
    public void onFailure(Call<DiscoverResponse<Movie>> call, Throwable t) {
        Log.d(LOG_TAG, "Error!");
        Log.d(LOG_TAG, t.getMessage());
        sendUpdateFinishedBroadcast();
    }

    private void sendUpdateFinishedBroadcast() {
        Intent intent = new Intent(BROADCAST_UPDATE_FINISHED);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private class SaveMoviesToDbTask extends AsyncTask<Response, Void, Void> {

        @Override
        protected Void doInBackground(Response... params) {
            Response<DiscoverResponse<Movie>> response = params[0];
            if (response != null && response.isSuccessful()) {
                Uri uri = SortingUtil.getSortedMoviesUri(MoviesService.this);
                if (uri == null) {
                    Log.w(LOG_TAG, "Wrong sorting.");
                    return null;
                }
                Log.d(LOG_TAG, "Successful!");
                Log.d(LOG_TAG, response.message());

                DiscoverResponse<Movie> discoverResponse = response.body();
                List<Movie> movies = discoverResponse.getResults();
                Log.d(LOG_TAG, movies.toString());

                for (int i = 0; i < movies.size(); i++) {
                    Uri movieUri = getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, movies.get(i).toContentValues());
                    long id = MoviesContract.MovieEntry.getIdFromUri(movieUri);
                    ContentValues entry = new ContentValues();
                    entry.put(MoviesContract.COLUMN_MOVIE_ID_KEY, id);
                    getContentResolver().insert(uri, entry);
                }

            }
            sendUpdateFinishedBroadcast();


            return null;
        }
    }
}
