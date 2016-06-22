package me.maxdev.popularmoviesapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

public class MoviesService implements Callback<DiscoverResponse<Movie>> {

    public static final String BROADCAST_UPDATE_FINISHED = "UpdateFinished";
    public static final String EXTRA_IS_SUCCESSFUL_UPDATED = "isSuccessfulUpdated";

    private static final int PAGE_SIZE = 20;
    private static final String LOG_TAG = "MoviesService";
    private static volatile MoviesService instance = null;

    private final Context context;

    public MoviesService(Context context) {
        if (instance != null) {
            throw new IllegalStateException("Already instantiated.");
        }
        this.context = context.getApplicationContext();
    }

    public static MoviesService getInstance(Context context) {
        synchronized (MoviesService.class) {
            if (instance == null) {
                instance = new MoviesService(context);
            }
        }
        return instance;
    }

    public void refreshMovies() {
        TheMovieDbService service = TheMovieDbClient.getInstance(context);
        String sort = SortUtil.getSortByPreference(context).toString();

        Call<DiscoverResponse<Movie>> call = service.discoverMovies(sort, null);

        call.enqueue(this);
    }

    public void loadMoreMovies() {
        TheMovieDbService service = TheMovieDbClient.getInstance(context);
        String sort = SortUtil.getSortByPreference(context).toString();
        Uri uri = SortUtil.getSortedMoviesUri(context);
        if (uri == null) {
            return;
        }

        // TODO: make this query in background thread
        Cursor movies = context.getContentResolver().query(
                uri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        int currentPage = 1;
        if (movies != null) {
            currentPage = (movies.getCount() - 1) / PAGE_SIZE + 1;
            movies.close();
        }

        Log.e("xxx", "Load movies. page = " + (currentPage + 1));
        Call<DiscoverResponse<Movie>> call = service.discoverMovies(sort, currentPage + 1);

        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<DiscoverResponse<Movie>> call, Response<DiscoverResponse<Movie>> response) {
        new SaveMoviesToDbTask().execute(response);
    }

    @Override
    public void onFailure(Call<DiscoverResponse<Movie>> call, Throwable t) {
        sendUpdateFinishedBroadcast(false);
    }

    private void sendUpdateFinishedBroadcast(boolean isSuccessfulUpdated) {
        Intent intent = new Intent(BROADCAST_UPDATE_FINISHED);
        intent.putExtra(EXTRA_IS_SUCCESSFUL_UPDATED, isSuccessfulUpdated);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private class SaveMoviesToDbTask extends AsyncTask<Response, Void, Void> {

        @Override
        protected Void doInBackground(Response... params) {
            Response<DiscoverResponse<Movie>> response = params[0];
            if (response != null && response.isSuccessful()) {
                Uri uri = SortUtil.getSortedMoviesUri(context);
                if (uri == null) {
                    return null;
                }
                Log.d(LOG_TAG, "Successful!");
                Log.d(LOG_TAG, response.message());

                DiscoverResponse<Movie> discoverResponse = response.body();
                int page = discoverResponse.getPage();
                if (page == 1) {
                    // TODO: clear table by uri
                }

                List<Movie> movies = discoverResponse.getResults();
                Log.d(LOG_TAG, movies.toString());

                for (int i = 0; i < movies.size(); i++) {
                    Uri movieUri = context.getContentResolver()
                            .insert(MoviesContract.MovieEntry.CONTENT_URI, movies.get(i).toContentValues());
                    long id = MoviesContract.MovieEntry.getIdFromUri(movieUri);

                    ContentValues entry = new ContentValues();
                    entry.put(MoviesContract.COLUMN_MOVIE_ID_KEY, id);
                    context.getContentResolver().insert(uri, entry);
                }

            } else {
                sendUpdateFinishedBroadcast(false);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            sendUpdateFinishedBroadcast(true);
        }
    }
}
