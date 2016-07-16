package me.maxdev.popularmoviesapp.data;

import android.os.Parcel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MovieReviewTest {

    private static final String REVIEW_ID = "aa213jnx3iu";
    private static final String AUTHOR = "John Doe";
    private static final String CONTENT = "Great movie!";
    private static final String URL = "http:/example.com/movieReview/1";

    private MovieReview movieReview;

    @Before
    public void setUp() throws Exception {
        movieReview = new MovieReview(REVIEW_ID);
        movieReview.setAuthor(AUTHOR);
        movieReview.setContent(CONTENT);
        movieReview.setReviewUrl(URL);
    }

    @After
    public void tearDown() throws Exception {
        movieReview = null;
    }

    @Test
    public void testGetters() throws Exception {
        assertEquals(REVIEW_ID, movieReview.getReviewId());
        assertEquals(AUTHOR, movieReview.getAuthor());
        assertEquals(CONTENT, movieReview.getContent());
        assertEquals(URL, movieReview.getReviewUrl());
    }

    @Test
    public void testParcelable() {
        Parcel parcel = Parcel.obtain();
        movieReview.writeToParcel(parcel, movieReview.describeContents());

        parcel.setDataPosition(0);

        MovieReview movieReviewFromParcel = MovieReview.CREATOR.createFromParcel(parcel);

        assertEquals(movieReview, movieReviewFromParcel);
    }
}