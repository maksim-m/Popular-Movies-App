package me.maxdev.popularmoviesapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import javax.inject.Inject;

import me.maxdev.popularmoviesapp.api.DiscoverAndSearchResponse;
import me.maxdev.popularmoviesapp.api.TheMovieDbService;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MoviesService {

    public static final String BROADCAST_UPDATE_FINISHED = "UpdateFinished";
    public static final String EXTRA_IS_SUCCESSFUL_UPDATED = "isSuccessfulUpdated";

    private static final int PAGE_SIZE = 20;
    private static final String LOG_TAG = "MoviesService";

    private SortHelper sortHelper;
    private final Context context;
    private volatile boolean loading = false;

    private TheMovieDbService theMovieDbService;

    @Inject
    public MoviesService(Context context, TheMovieDbService theMovieDbService, SortHelper sortHelper) {
        this.context = context.getApplicationContext();
        this.sortHelper = sortHelper;
        this.theMovieDbService = theMovieDbService;
    }

    public void refreshMovies() {
        if (loading) {
            return;
        }
        loading = true;

        String sort = sortHelper.getSortByPreference().toString();
        callDiscoverMovies(sort, null);
    }

    public boolean isLoading() {
        return loading;
    }

    public void loadMoreMovies() {
        if (loading) {
            return;
        }
        loading = true;
        String sort = sortHelper.getSortByPreference().toString();
        Uri uri = sortHelper.getSortedMoviesUri();
        if (uri == null) {
            return;
        }
        callDiscoverMovies(sort, getCurrentPage(uri) + 1);
    }

    private void callDiscoverMovies(String sort, @Nullable Integer page) {

        theMovieDbService.discoverMovies(sort, page)
                .subscribeOn(Schedulers.newThread())
                .doOnNext(discoverMoviesResponse -> clearMoviesSortTableIfNeeded(discoverMoviesResponse))
                .doOnNext(discoverMoviesResponse -> logResponse(discoverMoviesResponse))
                .map(discoverMoviesResponse -> discoverMoviesResponse.getResults())
                .flatMap(movies -> Observable.from(movies))
                .map(movie -> saveMovie(movie))
                .map(movieUri -> MoviesContract.MovieEntry.getIdFromUri(movieUri))
                .doOnNext(movieId -> saveMovieReference(movieId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                        loading = false;
                        sendUpdateFinishedBroadcast(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        loading = false;
                        sendUpdateFinishedBroadcast(false);
                        Log.e(LOG_TAG, "Error", e);
                    }

                    @Override
                    public void onNext(Long aLong) {
                        // do nothing
                    }
                });
    }

    private void saveMovieReference(Long movieId) {
        ContentValues entry = new ContentValues();
        entry.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieId);
        context.getContentResolver().insert(sortHelper.getSortedMoviesUri(), entry);
    }

    private Uri saveMovie(Movie movie) {
        return context.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, movie.toContentValues());
    }

    private void logResponse(DiscoverAndSearchResponse<Movie> discoverMoviesResponse) {
        Log.d(LOG_TAG, "page == " + discoverMoviesResponse.getPage() + " " +
                discoverMoviesResponse.getResults().toString());
    }

    private void clearMoviesSortTableIfNeeded(DiscoverAndSearchResponse<Movie> discoverMoviesResponse) {
        if (discoverMoviesResponse.getPage() == 1) {
            context.getContentResolver().delete(
                    sortHelper.getSortedMoviesUri(),
                    null,
                    null
            );
        }
    }

    private void sendUpdateFinishedBroadcast(boolean successfulUpdated) {
        Intent intent = new Intent(BROADCAST_UPDATE_FINISHED);
        intent.putExtra(EXTRA_IS_SUCCESSFUL_UPDATED, successfulUpdated);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private int getCurrentPage(Uri uri) {
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
        return currentPage;
    }
}
