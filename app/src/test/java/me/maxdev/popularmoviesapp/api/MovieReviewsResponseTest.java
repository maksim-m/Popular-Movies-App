package me.maxdev.popularmoviesapp.api;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import me.maxdev.popularmoviesapp.data.MovieReview;

import static org.junit.Assert.assertEquals;

public class MovieReviewsResponseTest {

    private static final long MOVIE_ID = 123;
    private static final int PAGE = 1;
    private static final ArrayList<MovieReview> MOVIE_REVIEWS = new ArrayList<>();

    static {
        MOVIE_REVIEWS.add(new MovieReview("abc123"));
        MOVIE_REVIEWS.add(new MovieReview("def456"));
    }

    private static final int TOTAL_PAGES = 3;

    private MovieReviewsResponse movieReviewsResponse;

    @Before
    public void setUp() throws Exception {
        movieReviewsResponse = new MovieReviewsResponse(MOVIE_ID, PAGE, MOVIE_REVIEWS, TOTAL_PAGES);
    }

    @After
    public void tearDown() throws Exception {
        movieReviewsResponse = null;
    }

    @Test
    public void testGetters() throws Exception {
        assertEquals(MOVIE_ID, movieReviewsResponse.getMovieId());
        assertEquals(PAGE, movieReviewsResponse.getPage());
        assertEquals(MOVIE_REVIEWS, movieReviewsResponse.getResults());
        assertEquals(TOTAL_PAGES, movieReviewsResponse.getTotalPages());
    }
}