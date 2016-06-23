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
    private volatile boolean loading = false;

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
        if (loading) {
            return;
        }
        loading = true;
        TheMovieDbService service = TheMovieDbClient.getInstance(context);
        String sort = SortUtil.getSortByPreference(context).toString();

        Call<DiscoverResponse<Movie>> call = service.discoverMovies(sort, null);

        call.enqueue(this);
    }

    public boolean isLoading() {
        return loading;
    }

    public void loadMoreMovies() {
        if (loading) {
            return;
        }
        loading = true;
        TheMovieDbService service = TheMovieDbClient.getInstance(context);
        String sort = SortUtil.getSortByPreference(context).toString();
        Uri uri = SortUtil.getSortedMoviesUri(context);
        if (uri == null) {
            return;
        }

        Cursor movies = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        int currentPage = 1;
        if (movies != null) {
            currentPage = (movies.getCount() - 1) / PAGE_SIZE + 1;
            movies.close();
        }

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
        loading = false;
    }

    private void sendUpdateFinishedBroadcast(boolean successfulUpdated) {
        Intent intent = new Intent(BROADCAST_UPDATE_FINISHED);
        intent.putExtra(EXTRA_IS_SUCCESSFUL_UPDATED, successfulUpdated);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private class SaveMoviesToDbTask extends AsyncTask<Response, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Response... params) {
            Response<DiscoverResponse<Movie>> response = params[0];
            if (response != null && response.isSuccessful()) {
                Uri uri = SortUtil.getSortedMoviesUri(context);
                if (uri == null) {
                    return false;
                }
                Log.d(LOG_TAG, "Successful!");
                Log.d(LOG_TAG, response.message());

                DiscoverResponse<Movie> discoverResponse = response.body();
                int page = discoverResponse.getPage();
                if (page == 1) {
                    context.getContentResolver().delete(
                            uri,
                            null,
                            null
                    );
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

                return true;

            } else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean successfulUpdated) {
            loading = false;
            sendUpdateFinishedBroadcast(successfulUpdated);
        }
    }
}
