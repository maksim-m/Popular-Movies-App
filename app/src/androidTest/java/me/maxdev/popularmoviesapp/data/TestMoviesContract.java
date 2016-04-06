package me.maxdev.popularmoviesapp.data;

import android.net.Uri;
import android.test.AndroidTestCase;

public class TestMoviesContract extends AndroidTestCase {

    private static final long TEST_MOVIE_ID = 157821;

    public void testBuildMovieUri() {

        Uri movieUri = MoviesContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);

        assertNotNull("Error: Null Uri returned.", movieUri);

        assertEquals("Error: Movie ID not properly appended to the end of the Uri",
                String.valueOf(TEST_MOVIE_ID), movieUri.getLastPathSegment());

        assertEquals("Error: Movie Uri doesn't match expected result",
                movieUri.toString(),
                "content://me.maxdev.popularmoviesapp/movies/157821");
    }
}
