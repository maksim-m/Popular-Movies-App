package me.maxdev.popularmoviesapp.data;

import android.net.Uri;
import android.test.AndroidTestCase;

public class TestMoviesContract extends AndroidTestCase {

    private static final long TEST_MOVIE_ID = 157821;

    public void testBaseContentUri() {
        assertNotNull("Error: Null Uri returned.", MoviesContract.BASE_CONTENT_URI);
        assertEquals("Error: Base Content Uri doesn't match expected result",
                MoviesContract.BASE_CONTENT_URI.toString(),
                "content://me.maxdev.popularmoviesapp");
    }

    public void testMoviesUri() {
        assertNotNull("Error: Null Uri returned.", MoviesContract.MovieEntry.CONTENT_URI);
        assertEquals("Error: Movies Uri doesn't match expected result",
                MoviesContract.MovieEntry.CONTENT_URI.toString(),
                "content://me.maxdev.popularmoviesapp/movies");
    }

    public void testBuildMovieUri() {

        Uri movieUri = MoviesContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);

        assertNotNull("Error: Null Uri returned.", movieUri);

        assertEquals("Error: Movie ID not properly appended to the end of the Uri",
                String.valueOf(TEST_MOVIE_ID), movieUri.getLastPathSegment());

        assertEquals("Error: Movie Uri doesn't match expected result",
                movieUri.toString(),
                "content://me.maxdev.popularmoviesapp/movies/157821");
    }

    public void testGetIdFromUri() {
        Uri movieUri = MoviesContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);
        assertEquals("Error: Movie ID doesn't match expected result",
                MoviesContract.MovieEntry.getIdFromUri(movieUri), TEST_MOVIE_ID);
    }

    public void testMostPopularMoviesUri() {
        assertNotNull("Error: Null Uri returned.", MoviesContract.MostPopularMovies.CONTENT_URI);
        assertEquals("Error: Most popular movies Uri doesn't match expected result",
                MoviesContract.MostPopularMovies.CONTENT_URI.toString(),
                "content://me.maxdev.popularmoviesapp/movies/most_popular");
    }

    public void testHighestRatedMoviesUri() {
        assertNotNull("Error: Null Uri returned.", MoviesContract.HighestRatedMovies.CONTENT_URI);
        assertEquals("Error: Highest rated movies Uri doesn't match expected result",
                MoviesContract.HighestRatedMovies.CONTENT_URI.toString(),
                "content://me.maxdev.popularmoviesapp/movies/highest_rated");
    }

    public void testMostRatedMoviesUri() {
        assertNotNull("Error: Null Uri returned.", MoviesContract.MostRatedMovies.CONTENT_URI);
        assertEquals("Error: Most rated movies Uri doesn't match expected result",
                MoviesContract.MostRatedMovies.CONTENT_URI.toString(),
                "content://me.maxdev.popularmoviesapp/movies/most_rated");
    }

    public void testFavoritesUri() {
        assertNotNull("Error: Null Uri returned.", MoviesContract.Favorites.CONTENT_URI);
        assertEquals("Error: Most rated movies Uri doesn't match expected result",
                MoviesContract.Favorites.CONTENT_URI.toString(),
                "content://me.maxdev.popularmoviesapp/movies/favorites");
    }
}
