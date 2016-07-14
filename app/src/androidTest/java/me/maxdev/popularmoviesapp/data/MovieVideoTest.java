package me.maxdev.popularmoviesapp.data;

import android.os.Parcel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MovieVideoTest {

    private static final String VIDEO_ID = "533ec66ac3a3685448001ab8";
    private static final String LANGUAGE_CODE = "en";
    private static final String COUNTRY_CODE = "US";
    private static final String KEY = "yUQM7H4Swgw";
    private static final String NAME = "Trailer";
    private static final String SITE = "YouTube";
    private static final int SIZE = 720;
    private static final String TYPE = "Trailer";

    private MovieVideo movieVideo = null;

    @Before
    public void setUp() throws Exception {
        movieVideo = new MovieVideo(VIDEO_ID);
        movieVideo.setVideoId(VIDEO_ID);
        movieVideo.setLanguageCode(LANGUAGE_CODE);
        movieVideo.setCountryCode(COUNTRY_CODE);
        movieVideo.setKey(KEY);
        movieVideo.setName(NAME);
        movieVideo.setSite(SITE);
        movieVideo.setSize(SIZE);
        movieVideo.setType(TYPE);
    }

    @After
    public void tearDown() throws Exception {
        movieVideo = null;
    }

    @Test
    public void testGetters() throws Exception {
        assertEquals(VIDEO_ID, movieVideo.getVideoId());
        assertEquals(LANGUAGE_CODE, movieVideo.getLanguageCode());
        assertEquals(COUNTRY_CODE, movieVideo.getCountryCode());
        assertEquals(KEY, movieVideo.getKey());
        assertEquals(NAME, movieVideo.getName());
        assertEquals(SITE, movieVideo.getSite());
        assertEquals(SIZE, movieVideo.getSize());
        assertEquals(TYPE, movieVideo.getType());

        assertEquals(true, movieVideo.isYoutubeVideo());
        movieVideo.setSite("AnotherSite");
        assertEquals(false, movieVideo.isYoutubeVideo());
    }

    @Test
    public void testParcelable() {
        Parcel parcel = Parcel.obtain();
        movieVideo.writeToParcel(parcel, movieVideo.describeContents());

        parcel.setDataPosition(0);

        MovieVideo movieVideoFromParcel = MovieVideo.CREATOR.createFromParcel(parcel);

        assertEquals(movieVideo, movieVideoFromParcel);
    }

}