package me.maxdev.popularmoviesapp.data;


import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import me.maxdev.popularmoviesapp.utils.PollingCheck;

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

    static ContentValues createTestMovieValues() {
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

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
