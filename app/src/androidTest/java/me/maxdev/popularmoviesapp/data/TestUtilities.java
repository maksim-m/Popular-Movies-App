package me.maxdev.popularmoviesapp.data;


import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesContract.MovieEntry._ID, 10378);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Big Buck Bunny");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "Follow a day of the life of Big Buck Bunny when he meets three bullying rodents: Frank, Rinky, and Gamera. The rodents amuse themselves by harassing helpless creatures by throwing fruits, nuts and rocks at them. After the deaths of two of Bunny's favorite butterflies, and an offensive attack on Bunny himself, Bunny sets aside his gentle nature and orchestrates a complex plan for revenge.");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "2008-05-30");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "https://image.tmdb.org/t/p/original/uVEFQvFMMsg4e6yb03xOfVsDz4o.jpg");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, 1.5);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, "Big Buck Bunny");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_AVERAGE_VOTE, 6.5);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT, 33);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, "https://image.tmdb.org/t/p/original/1O3tFuQsVgmjwx47xGKBjkSUiU6.jpg");
        return movieValues;
    }

    static ContentValues createConflictedMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesContract.MovieEntry._ID, 10378);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Another Title");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "muse themselves by harassing helpless creatures by throwing fruits, nuts and rocks at them. After the deaths of two of Bunny's favorite butterflies, and an offensive attack on Bunny himself, Bunny sets aside his gentle nature and orchestrates a complex plan for revenge.");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "2025-05-30");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "https://image.tmdb.org/t/p/original/abc.jpg");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, 3.7);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, "Another Title");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_AVERAGE_VOTE, 5.7);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT, 42);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, "https://image.tmdb.org/t/p/original/abcdef.jpg");
        return movieValues;
    }
}
