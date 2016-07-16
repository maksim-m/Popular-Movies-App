package me.maxdev.popularmoviesapp.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import me.maxdev.popularmoviesapp.data.MovieVideo;

import static org.junit.Assert.assertEquals;

public class MovieVideosResponseTest {

    private static final long MOVIE_ID = 123456;
    private static final ArrayList<MovieVideo> MOVIE_VIDEOS = new ArrayList<>();

    static {
        MOVIE_VIDEOS.add(new MovieVideo("abc123"));
        MOVIE_VIDEOS.add(new MovieVideo("def567"));
    }

    private MovieVideosResponse movieVideosResponse;

    @Before
    public void setUp() throws Exception {
        movieVideosResponse = new MovieVideosResponse(MOVIE_ID, MOVIE_VIDEOS);
    }

    @After
    public void tearDown() throws Exception {
        movieVideosResponse = null;
    }

    @Test
    public void testGetters() throws Exception {
        assertEquals(MOVIE_ID, movieVideosResponse.getMovieId());
        assertEquals(MOVIE_VIDEOS, movieVideosResponse.getResults());
    }
}