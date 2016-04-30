package me.maxdev.popularmoviesapp.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {

    private static final long TEST_MOVIE_ID = 157821;

    // content://me.maxdev.popularmoviesapp/
    private static final Uri TEST_BASE_URI = MoviesContract.BASE_CONTENT_URI;
    // content://me.maxdev.popularmoviesapp/movies
    private static final Uri TEST_MOVIES_URI = MoviesContract.MovieEntry.CONTENT_URI;
    // content://me.maxdev.popularmoviesapp/movies/157821
    private static final Uri TEST_MOVIE_BY_ID_URI = MoviesContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);

    // content://me.maxdev.popularmoviesapp/movies/most_popular
    private static final Uri TEST_MOST_POPULAR_MOVIES_URI = MoviesContract.MostPopularMovies.CONTENT_URI;
    // content://me.maxdev.popularmoviesapp/movies/highest_rated
    private static final Uri TEST_HIGHEST_RATED_MOVIES_URI = MoviesContract.HighestRatedMovies.CONTENT_URI;
    // content://me.maxdev.popularmoviesapp/movies/most_rated
    private static final Uri TEST_MOST_RATED_MOVIES_URI = MoviesContract.MostRatedMovies.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher uriMatcher = MoviesProvider.buildUriMatcher();

        assertEquals("Error: The BASE CONTENT URI was matched incorrectly.",
                uriMatcher.match(TEST_BASE_URI), UriMatcher.NO_MATCH);
        assertEquals("Error: The MOVIES URI was matched incorrectly.",
                uriMatcher.match(TEST_MOVIES_URI), MoviesProvider.MOVIES);
        assertEquals("Error: The MOVIE BY ID URI was matched incorrectly.",
                uriMatcher.match(TEST_MOVIE_BY_ID_URI), MoviesProvider.MOVIE_BY_ID);

        assertEquals("Error: The MOST POPULAR MOVIES URI was matched incorrectly.",
                uriMatcher.match(TEST_MOST_POPULAR_MOVIES_URI), MoviesProvider.MOST_POPULAR_MOVIES);
        assertEquals("Error: The HIGHEST RATED MOVIES URI was matched incorrectly.",
                uriMatcher.match(TEST_HIGHEST_RATED_MOVIES_URI), MoviesProvider.HIGHEST_RATED_MOVIES);
        assertEquals("Error: The MOST RATED MOVIES URI was matched incorrectly.",
                uriMatcher.match(TEST_MOST_RATED_MOVIES_URI), MoviesProvider.MOST_RATED_MOVIES);
    }
}
