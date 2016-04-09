package me.maxdev.popularmoviesapp.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;

public class TestProvider extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // MoviesProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MoviesProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MoviesProvider registered with wrong authority " + providerInfo.authority,
                    providerInfo.authority, MoviesContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MoviesProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        // content://me.maxdev.popularmoviesapp/movies
        String type = mContext.getContentResolver().getType(MoviesContract.MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/me.maxdev.popularmoviesapp/movies
        assertEquals("Error: the MOVIES CONTENT URI should return MovieEntry.CONTENT_DIR_TYPE",
                MoviesContract.MovieEntry.CONTENT_DIR_TYPE, type);

        long TEST_MOVIE_ID = 157821;
        // content://me.maxdev.popularmoviesapp/movies/157821
        type = mContext.getContentResolver().getType(MoviesContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID));
        // vnd.android.cursor.item/me.maxdev.popularmoviesapp/movies/157821
        assertEquals("Error: the MOVIE BY ID CONTENT URI should return MovieEntry.CONTENT_ITEM_TYPE",
                MoviesContract.MovieEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testMoviesQuery() {
        ContentValues testValues = insertTestValues();

        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (movies == null) {
            fail("Get empty cursor by querying movies.");
        }
        TestUtilities.validateCursor("Error by querying movies.", movies, testValues);

        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Movies Query did not properly set NotificationUri",
                    movies.getNotificationUri(), MoviesContract.MovieEntry.CONTENT_URI);
        }
    }

    public void testMovieByIdQuery() {
        ContentValues testValues = insertTestValues();
        long testMovieId = testValues.getAsLong(MoviesContract.MovieEntry._ID);
        Uri testMovieUri = MoviesContract.MovieEntry.buildMovieUri(testMovieId);

        Cursor movie = mContext.getContentResolver().query(
                testMovieUri,
                null,
                null,
                null,
                null
        );
        if (movie == null) {
            fail("Get empty cursor by querying movie by id.");
        }
        TestUtilities.validateCursor("Error by querying movie by id.", movie, testValues);
        assertEquals("Movie by ID query returned more than one entry. ", movie.getCount(), 1);

        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Movie by ID Query did not properly set NotificationUri",
                    movie.getNotificationUri(), testMovieUri);
        }
    }

    public void testInsert() {
        ContentValues testValues = TestUtilities.createTestMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);

        // Did our content observer get called?
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsert. Error validating MovieEntry.",
                cursor, testValues);

        // Test replace police
        ContentValues conflictedValues = TestUtilities.createConflictedMovieValues();
        tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MovieEntry.CONTENT_URI, true, tco);
        movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, conflictedValues);

        // Did our content observer get called?
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        cursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsert. Error validating MovieEntry.",
                cursor, conflictedValues);
    }

    public void testInsert2() {
        // Test content notification for MOVIE BY ID Uri
        ContentValues testValues = TestUtilities.createTestMovieValues();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        mContext.getContentResolver().registerContentObserver(movieUri, true, tco);
        long movieId = MoviesContract.MovieEntry.getIdFromUri(movieUri);
        assertEquals(movieUri, MoviesContract.MovieEntry.buildMovieUri(movieId));

        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.buildMovieUri(movieId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsert. Error validating MovieEntry.",
                cursor, testValues);

        // Replace
        ContentValues conflictedValues = TestUtilities.createConflictedMovieValues();
        movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, conflictedValues);
        assertTrue(movieUri != null);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        cursor = mContext.getContentResolver().query(
                movieUri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsert. Error validating MovieEntry.",
                cursor, conflictedValues);
    }

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movies table during delete", 0, cursor.getCount());
        cursor.close();
    }

    /*
      This helper function deletes all records from both database tables using the database
      functions only.  This is designed to be used to reset the state of the database until the
      delete functionality is available in the ContentProvider.
    */
    public void deleteAllRecordsFromDB() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MoviesContract.MovieEntry.TABLE_NAME, null, null);
        db.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    ContentValues insertTestValues() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createTestMovieValues();
        long id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, testValues);
        if (id == -1) {
            fail("Error by inserting contentValues into database.");
        }
        db.close();
        return testValues;
    }
}
