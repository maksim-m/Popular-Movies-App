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
}
