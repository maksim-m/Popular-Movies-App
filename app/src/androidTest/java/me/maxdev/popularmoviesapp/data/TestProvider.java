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
        movies.close();
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
        movie.close();
    }

    public void testInsert() {
        ContentValues testValues = TestUtilities.createTestMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver moviesObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MovieEntry.CONTENT_URI, true, moviesObserver);

        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);

        // Did our content observer get called?
        moviesObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(moviesObserver);

        long movieRowId = ContentUris.parseId(movieUri);
        // Verify we got a row back.
        assertTrue(movieRowId != -1);
        assertEquals(MoviesContract.MovieEntry.buildMovieUri(movieRowId), movieUri);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.
        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsert. Error validating MovieEntry.",
                movies, testValues);

        // Test replace police
        ContentValues conflictedValues = TestUtilities.createConflictedMovieValues();

        moviesObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MovieEntry.CONTENT_URI, true, moviesObserver);

        TestUtilities.TestContentObserver movieByIdObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(movieUri, true, movieByIdObserver);

        movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, conflictedValues);
        assertTrue(movieUri != null);

        movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);
        assertEquals(MoviesContract.MovieEntry.buildMovieUri(movieRowId), movieUri);

        // Did our content observer get called?
        moviesObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(moviesObserver);
        movieByIdObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieByIdObserver);

        movies = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertTrue(movies != null);
        TestUtilities.validateCursor("testInsert. Error validating MovieEntry.",
                movies, conflictedValues);
        movies.close();
    }

    public void testUpdateMovie() {
        ContentValues testValues = TestUtilities.createTestMovieValues();

        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieId = ContentUris.parseId(movieUri);
        assertTrue(movieId != -1);
        assertEquals(movieUri, MoviesContract.MovieEntry.buildMovieUri(movieId));

        ContentValues updatedValues = new ContentValues(testValues);
        updatedValues.put(MoviesContract.MovieEntry._ID, movieId);
        updatedValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT, 100500);

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        assertTrue(movies != null);

        TestUtilities.TestContentObserver moviesObserver = TestUtilities.getTestContentObserver();
        movies.registerContentObserver(moviesObserver);

        TestUtilities.TestContentObserver movieByIdObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(movieUri, true, movieByIdObserver);

        int count = mContext.getContentResolver().update(
                MoviesContract.MovieEntry.CONTENT_URI, updatedValues,
                MoviesContract.MovieEntry._ID + "= ?", new String[] { Long.toString(movieId)});
        assertEquals(1, count);

        // Test to make sure our observer is called.
        moviesObserver.waitForNotificationOrFail();

        movies.unregisterContentObserver(moviesObserver);
        movies.close();

        movieByIdObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieByIdObserver);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,   // projection
                MoviesContract.MovieEntry._ID + " = " + movieId,
                null,   // Values for the "where" clause
                null    // sort order
        );
        assertTrue(cursor != null);
        TestUtilities.validateCursor("testUpdateMovie.  Error validating movie entry update.",
                cursor, updatedValues);

        cursor.close();
    }

    public void testDeleteAllMovies() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long id = ContentUris.parseId(movieUri);
        assertTrue(id != -1);
        assertEquals(MoviesContract.MovieEntry.buildMovieUri(id), movieUri);

        TestUtilities.TestContentObserver moviesObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MovieEntry.CONTENT_URI, true, moviesObserver);

        TestUtilities.TestContentObserver movieByIdObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(movieUri, true, movieByIdObserver);

        deleteAllRecordsFromProvider();

        moviesObserver.waitForNotificationOrFail();
        movieByIdObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(moviesObserver);
        mContext.getContentResolver().unregisterContentObserver(movieByIdObserver);
    }

    public void testDeleteMovieById() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long id = ContentUris.parseId(movieUri);
        assertTrue(id != -1);
        assertEquals(MoviesContract.MovieEntry.buildMovieUri(id), movieUri);

        TestUtilities.TestContentObserver moviesObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MovieEntry.CONTENT_URI, true, moviesObserver);

        TestUtilities.TestContentObserver movieByIdObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(movieUri, true, movieByIdObserver);

        mContext.getContentResolver().delete(
                MoviesContract.MovieEntry.buildMovieUri(id),
                null,
                null
        );

        moviesObserver.waitForNotificationOrFail();
        movieByIdObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(moviesObserver);
        mContext.getContentResolver().unregisterContentObserver(movieByIdObserver);
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
        assertTrue(cursor != null);
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
