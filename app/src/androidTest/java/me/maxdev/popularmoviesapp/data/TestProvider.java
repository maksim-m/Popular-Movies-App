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

    private static final Uri INVALID_URI = new Uri.Builder()
            .scheme("http")
            .authority("example.com")
            .appendPath("test")
            .build();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
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

        // content://me.maxdev.popularmoviesapp/movies/most_popular
        type = mContext.getContentResolver().getType(MoviesContract.MostPopularMovies.CONTENT_URI);
        assertEquals("Error: the MOST POPULAR MOVIES CONTENT URI should return MostPopularMovies.CONTENT_DIR_TYPE",
                MoviesContract.MostPopularMovies.CONTENT_DIR_TYPE, type);

        // content://me.maxdev.popularmoviesapp/movies/highest_rated
        type = mContext.getContentResolver().getType(MoviesContract.HighestRatedMovies.CONTENT_URI);
        assertEquals("Error: the HIGHEST RATED MOVIES CONTENT URI should return HighestRatedMovies.CONTENT_DIR_TYPE",
                MoviesContract.HighestRatedMovies.CONTENT_DIR_TYPE, type);

        // content://me.maxdev.popularmoviesapp/movies/most_rated
        type = mContext.getContentResolver().getType(MoviesContract.MostRatedMovies.CONTENT_URI);
        assertEquals("Error: the MOST RATED MOVIES CONTENT URI should return MostRatedMovies.CONTENT_DIR_TYPE",
                MoviesContract.MostRatedMovies.CONTENT_DIR_TYPE, type);

        // content://me.maxdev.popularmoviesapp/movies/favorites
        type = mContext.getContentResolver().getType(MoviesContract.Favorites.CONTENT_URI);
        assertEquals("Error: the FAVORITES CONTENT URI should return MostRatedMovies.CONTENT_DIR_TYPE",
                MoviesContract.Favorites.CONTENT_DIR_TYPE, type);

        assertTrue(mContext.getContentResolver().getType(INVALID_URI) == null);
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

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movies Query did not properly set NotificationUri",
                    movies.getNotificationUri(), MoviesContract.MovieEntry.CONTENT_URI);
        }
        movies.close();

        try {
            mContext.getContentResolver().query(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    new String[]{"Invalid column"},
                    null,
                    null,
                    null
            );
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Unknown columns in projection.", e.getMessage());
        }

        assertNull(
                mContext.getContentResolver().query(
                        INVALID_URI,
                        null,
                        null,
                        null,
                        null
                )
        );
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

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movie by ID Query did not properly set NotificationUri",
                    movie.getNotificationUri(), testMovieUri);
        }
        movie.close();
    }

    public void testMostPopularMoviesQuery() {
        ContentValues testValues = insertTestValues();
        long movieId = testValues.getAsLong(MoviesContract.MovieEntry._ID);
        insertSortTableTestValues(MoviesContract.MostPopularMovies.TABLE_NAME, movieId);

        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.MostPopularMovies.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (movies == null) {
            fail("Get empty cursor by querying movies.");
        }
        TestUtilities.validateCursor("Error by querying movies.", movies, testValues);

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movies Query did not properly set NotificationUri",
                    MoviesContract.MostPopularMovies.CONTENT_URI, movies.getNotificationUri());
        }
        movies.close();
    }

    public void testHighestRatedMoviesQuery() {
        ContentValues testValues = insertTestValues();
        long movieId = testValues.getAsLong(MoviesContract.MovieEntry._ID);
        insertSortTableTestValues(MoviesContract.HighestRatedMovies.TABLE_NAME, movieId);

        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.HighestRatedMovies.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (movies == null) {
            fail("Get empty cursor by querying movies.");
        }
        TestUtilities.validateCursor("Error by querying movies.", movies, testValues);

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movies Query did not properly set NotificationUri",
                    MoviesContract.HighestRatedMovies.CONTENT_URI, movies.getNotificationUri());
        }
        movies.close();
    }

    public void testFavoritesQuery() {
        ContentValues testValues = insertTestValues();
        long movieId = testValues.getAsLong(MoviesContract.MovieEntry._ID);
        insertSortTableTestValues(MoviesContract.Favorites.TABLE_NAME, movieId);

        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.Favorites.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (movies == null) {
            fail("Get empty cursor by querying movies.");
        }
        TestUtilities.validateCursor("Error by querying movies.", movies, testValues);

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movies Query did not properly set NotificationUri",
                    MoviesContract.Favorites.CONTENT_URI, movies.getNotificationUri());
        }
        movies.close();
    }

    public void testMostRatedMoviesQuery() {
        ContentValues testValues = insertTestValues();
        long movieId = testValues.getAsLong(MoviesContract.MovieEntry._ID);
        insertSortTableTestValues(MoviesContract.MostRatedMovies.TABLE_NAME, movieId);

        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.MostRatedMovies.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        if (movies == null) {
            fail("Get empty cursor by querying movies.");
        }
        TestUtilities.validateCursor("Error by querying movies.", movies, testValues);

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Movies Query did not properly set NotificationUri",
                    MoviesContract.MostRatedMovies.CONTENT_URI, movies.getNotificationUri());
        }
        movies.close();
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

    public void testInsertMostPopularMovie() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MostPopularMovies.CONTENT_URI, true, observer);

        ContentValues entryValues = new ContentValues();
        entryValues.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieRowId);

        Uri entryUri = mContext.getContentResolver().insert(MoviesContract.MostPopularMovies.CONTENT_URI, entryValues);
        assertTrue(entryUri != null);

        // Did our content observer get called?
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.MostPopularMovies.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(movies);
        TestUtilities.validateCursor("Error validating MovieEntry.", movies, entryValues);

        movies.close();
    }

    public void testInsertHighestRatedMovie() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.HighestRatedMovies.CONTENT_URI, true, observer);

        ContentValues entryValues = new ContentValues();
        entryValues.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieRowId);

        Uri entryUri = mContext.getContentResolver().insert(MoviesContract.HighestRatedMovies.CONTENT_URI, entryValues);
        assertTrue(entryUri != null);

        // Did our content observer get called?
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.HighestRatedMovies.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(movies);
        TestUtilities.validateCursor("Error validating MovieEntry", movies, entryValues);

        movies.close();
    }

    public void testInsertMostRatedMovie() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MostRatedMovies.CONTENT_URI, true, observer);

        ContentValues entryValues = new ContentValues();
        entryValues.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieRowId);

        Uri entryUri = mContext.getContentResolver().insert(MoviesContract.MostRatedMovies.CONTENT_URI, entryValues);
        assertTrue(entryUri != null);

        // Did our content observer get called?
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.MostRatedMovies.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(movies);
        TestUtilities.validateCursor("Error validating MovieEntry", movies, entryValues);

        movies.close();
    }

    public void testInsertFavorite() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.Favorites.CONTENT_URI, true, observer);

        ContentValues entryValues = new ContentValues();
        entryValues.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieRowId);

        Uri entryUri = mContext.getContentResolver().insert(MoviesContract.Favorites.CONTENT_URI, entryValues);
        assertTrue(entryUri != null);

        // Did our content observer get called?
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.Favorites.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(movies);
        TestUtilities.validateCursor("Error validating MovieEntry", movies, entryValues);

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
                MoviesContract.MovieEntry._ID + "= ?", new String[]{Long.toString(movieId)});
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

    public void testDeleteMostPopularMovies() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        ContentValues entryValues = new ContentValues();
        entryValues.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieRowId);

        Uri entryUri = mContext.getContentResolver().insert(MoviesContract.MostPopularMovies.CONTENT_URI, entryValues);
        assertTrue(entryUri != null);

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MostPopularMovies.CONTENT_URI, true, observer);

        mContext.getContentResolver().delete(
                MoviesContract.MostPopularMovies.CONTENT_URI,
                null,
                null
        );

        // Did our content observer get called?
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.MostPopularMovies.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(movies);
        assertTrue(movies.getCount() == 0);

        movies.close();
    }

    public void testDeleteHighestRatedMovies() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        ContentValues entryValues = new ContentValues();
        entryValues.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieRowId);

        Uri entryUri = mContext.getContentResolver().insert(MoviesContract.HighestRatedMovies.CONTENT_URI, entryValues);
        assertTrue(entryUri != null);

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.HighestRatedMovies.CONTENT_URI, true, observer);

        mContext.getContentResolver().delete(
                MoviesContract.HighestRatedMovies.CONTENT_URI,
                null,
                null
        );

        // Did our content observer get called?
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.HighestRatedMovies.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(movies);
        assertTrue(movies.getCount() == 0);

        movies.close();
    }

    public void testDeleteMostRatedMovies() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        ContentValues entryValues = new ContentValues();
        entryValues.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieRowId);

        Uri entryUri = mContext.getContentResolver().insert(MoviesContract.MostRatedMovies.CONTENT_URI, entryValues);
        assertTrue(entryUri != null);

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MostRatedMovies.CONTENT_URI, true, observer);

        mContext.getContentResolver().delete(
                MoviesContract.MostRatedMovies.CONTENT_URI,
                null,
                null
        );

        // Did our content observer get called?
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.MostRatedMovies.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(movies);
        assertTrue(movies.getCount() == 0);

        movies.close();
    }

    public void testDeleteFavorites() {
        ContentValues testValues = TestUtilities.createTestMovieValues();
        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);
        assertTrue(movieUri != null);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue(movieRowId != -1);

        ContentValues entryValues = new ContentValues();
        entryValues.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieRowId);

        Uri entryUri = mContext.getContentResolver().insert(MoviesContract.Favorites.CONTENT_URI, entryValues);
        assertTrue(entryUri != null);

        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.Favorites.CONTENT_URI, true, observer);

        mContext.getContentResolver().delete(
                MoviesContract.Favorites.CONTENT_URI,
                null,
                null
        );

        // Did our content observer get called?
        observer.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(observer);

        Cursor movies = mContext.getContentResolver().query(
                MoviesContract.Favorites.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(movies);
        assertTrue(movies.getCount() == 0);

        movies.close();
    }

    public void testBulkInsert() {
        deleteAllRecords();
        ContentValues[] bulkInsertContentValues = createBulkInsertValues();

        TestUtilities.TestContentObserver moviesObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(
                MoviesContract.MovieEntry.CONTENT_URI, true, moviesObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(
                MoviesContract.MovieEntry.CONTENT_URI, bulkInsertContentValues);

        moviesObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(moviesObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);
        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertTrue(cursor != null);
        assertEquals(BULK_INSERT_RECORDS_TO_INSERT, cursor.getCount());
        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MovieEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 25;

    static ContentValues[] createBulkInsertValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues values = new ContentValues();
            values.put(MoviesContract.MovieEntry._ID, i);
            values.put(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE, "Test movie" + i);
            values.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "Test");
            values.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "13.04.2016");
            values.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "http://example.com/" + i);
            values.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, 1.2 + i);
            values.put(MoviesContract.MovieEntry.COLUMN_TITLE, "Test movie" + i);
            values.put(MoviesContract.MovieEntry.COLUMN_AVERAGE_VOTE, 1.2 + i);
            values.put(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT, 5 * i + 1);
            values.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, "http://example.com/" + i);
            returnContentValues[i] = values;
        }
        return returnContentValues;
    }

    public void deleteAllRecordsFromProvider() {
        clearTableByUri(MoviesContract.MovieEntry.CONTENT_URI);
        clearTableByUri(MoviesContract.MostPopularMovies.CONTENT_URI);
        clearTableByUri(MoviesContract.HighestRatedMovies.CONTENT_URI);
        clearTableByUri(MoviesContract.MostRatedMovies.CONTENT_URI);
        clearTableByUri(MoviesContract.Favorites.CONTENT_URI);
    }

    public void clearTableByUri(Uri uri) {
        mContext.getContentResolver().delete(
                uri,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );
        assertTrue(cursor != null);
        assertEquals("Error: Records not deleted", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    private ContentValues insertTestValues() {
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

    private void insertSortTableTestValues(String tableName, long movieId) {
        MoviesDbHelper dbHelper = new MoviesDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = new ContentValues();
        testValues.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieId);
        long id = db.insert(tableName, null, testValues);
        if (id == -1) {
            fail("Error by inserting contentValues into " + tableName);
        }
        db.close();
    }
}
