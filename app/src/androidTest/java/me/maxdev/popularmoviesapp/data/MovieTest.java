package me.maxdev.popularmoviesapp.data;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MovieTest {

    private static final int ID = 10378;
    private static final String TITLE = "Big Buck Bunny";
    private static final String OVERVIEW = "Follow a day of the life of Big Buck Bunny when he meets three bullying rodents: Frank, Rinky, and Gamera. The rodents amuse themselves by harassing helpless creatures by throwing fruits, nuts and rocks at them. After the deaths of two of Bunny's favorite butterflies, and an offensive attack on Bunny himself, Bunny sets aside his gentle nature and orchestrates a complex plan for revenge.";
    private static final String RELEASE_DATE = "2008-05-30";
    private static final String POSTER_PATH = "https://image.tmdb.org/t/p/original/uVEFQvFMMsg4e6yb03xOfVsDz4o.jpg";
    private static final double POPULARITY = 1.5;
    private static final String ORIGINAL_TITLE = "Big Buck Bunny";
    private static final double AVERAGE_VOTE = 6.5;
    private static final int VOTE_COUNT = 33;
    private static final String BACKDROP_PATH = "https://image.tmdb.org/t/p/original/1O3tFuQsVgmjwx47xGKBjkSUiU6.jpg";

    private Movie movie;

    @Before
    public void setUp() throws Exception {
        movie = new Movie(ID, TITLE);
        movie.setOverview(OVERVIEW);
        movie.setReleaseDate(RELEASE_DATE);
        movie.setPosterPath(POSTER_PATH);
        movie.setPopularity(POPULARITY);
        movie.setOriginalTitle(ORIGINAL_TITLE);
        movie.setAverageVote(AVERAGE_VOTE);
        movie.setVoteCount(VOTE_COUNT);
        movie.setBackdropPath(BACKDROP_PATH);
    }

    @After
    public void tearDown() throws Exception {
        movie = null;
    }

    @Test
    public void testGetters() throws Exception {
        assertEquals(ID, movie.getId());
        assertEquals(TITLE, movie.getTitle());
        assertEquals(OVERVIEW, movie.getOverview());
        assertEquals(RELEASE_DATE, movie.getReleaseDate());
        assertEquals(POSTER_PATH, movie.getPosterPath());
        assertEquals(POPULARITY, movie.getPopularity(), 1e-5);
        assertEquals(ORIGINAL_TITLE, movie.getOriginalTitle());
        assertEquals(AVERAGE_VOTE, movie.getAverageVote(), 1e-5);
        assertEquals(VOTE_COUNT, movie.getVoteCount());
        assertEquals(BACKDROP_PATH, movie.getBackdropPath());
    }

    @Test
    public void testParcelable() {
        Parcel parcel = Parcel.obtain();
        movie.writeToParcel(parcel, movie.describeContents());

        parcel.setDataPosition(0);

        Movie movieFromParcel = Movie.CREATOR.createFromParcel(parcel);

        assertEquals(movie, movieFromParcel);
    }

}