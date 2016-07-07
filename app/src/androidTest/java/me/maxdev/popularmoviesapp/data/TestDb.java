package me.maxdev.popularmoviesapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Arrays;
import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MoviesContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MoviesContract.MostPopularMovies.TABLE_NAME);
        tableNameHashSet.add(MoviesContract.HighestRatedMovies.TABLE_NAME);
        tableNameHashSet.add(MoviesContract.MostRatedMovies.TABLE_NAME);
        tableNameHashSet.add(MoviesContract.Favorites.TABLE_NAME);

        SQLiteDatabase db = new MoviesDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());
        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());
        assertTrue("Error. The database doesn't contain all of the required tables", tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        checkTableColumns(db, MoviesContract.MovieEntry.TABLE_NAME, MoviesContract.MovieEntry.getColumns());
        checkTableColumns(db, MoviesContract.MostPopularMovies.TABLE_NAME, MoviesContract.MostPopularMovies.getColumns());
        checkTableColumns(db, MoviesContract.HighestRatedMovies.TABLE_NAME, MoviesContract.HighestRatedMovies.getColumns());
        checkTableColumns(db, MoviesContract.MostRatedMovies.TABLE_NAME, MoviesContract.MostRatedMovies.getColumns());
        checkTableColumns(db, MoviesContract.Favorites.TABLE_NAME, MoviesContract.Favorites.getColumns());

        c.close();
        db.close();
    }

    public void testMoviesTable() {
        insertMovie();
    }

    public void testMostPopularMoviesTable() {
        long movieId = insertMovie();

        MoviesDbHelper helper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieId);

        long id = db.insert(MoviesContract.MostPopularMovies.TABLE_NAME, null, contentValues);
        if (id == -1) {
            fail("Error by inserting contentValues into database.");
        }
        Cursor cursor = db.query(
                MoviesContract.MostPopularMovies.TABLE_NAME,
                null,       // all columns
                null,       // Columns for the "where" clause
                null,       // Values for the "where" clause
                null,       // columns to group by
                null,       // columns to filter by row groups
                null        // sort order
        );
        contentValues.put(MoviesContract.MostPopularMovies._ID, id);
        assertTrue("Error: No Records returned from query", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Query Validation Failed", cursor, contentValues);
        assertFalse("Error: More than one record returned from query", cursor.moveToNext());
    }

    public void testHighestRatedMoviesTable() {
        long movieId = insertMovie();

        MoviesDbHelper helper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieId);

        long id = db.insert(MoviesContract.HighestRatedMovies.TABLE_NAME, null, contentValues);
        if (id == -1) {
            fail("Error by inserting contentValues into database.");
        }
        Cursor cursor = db.query(
                MoviesContract.HighestRatedMovies.TABLE_NAME,
                null,       // all columns
                null,       // Columns for the "where" clause
                null,       // Values for the "where" clause
                null,       // columns to group by
                null,       // columns to filter by row groups
                null        // sort order
        );
        contentValues.put(MoviesContract.HighestRatedMovies._ID, id);
        assertTrue("Error: No Records returned from query", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Query Validation Failed", cursor, contentValues);
        assertFalse("Error: More than one record returned from query", cursor.moveToNext());
    }

    public void testMostRatedMoviesTable() {
        long movieId = insertMovie();

        MoviesDbHelper helper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieId);

        long id = db.insert(MoviesContract.MostRatedMovies.TABLE_NAME, null, contentValues);
        if (id == -1) {
            fail("Error by inserting contentValues into database.");
        }
        Cursor cursor = db.query(
                MoviesContract.MostRatedMovies.TABLE_NAME,
                null,       // all columns
                null,       // Columns for the "where" clause
                null,       // Values for the "where" clause
                null,       // columns to group by
                null,       // columns to filter by row groups
                null        // sort order
        );
        contentValues.put(MoviesContract.MostRatedMovies._ID, id);
        assertTrue("Error: No Records returned from query", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Query Validation Failed", cursor, contentValues);
        assertFalse("Error: More than one record returned from query", cursor.moveToNext());
    }

    public void testFavoritesTable() {
        long movieId = insertMovie();

        MoviesDbHelper helper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movieId);

        long id = db.insert(MoviesContract.Favorites.TABLE_NAME, null, contentValues);
        if (id == -1) {
            fail("Error by inserting contentValues into database.");
        }
        Cursor cursor = db.query(
                MoviesContract.Favorites.TABLE_NAME,
                null,       // all columns
                null,       // Columns for the "where" clause
                null,       // Values for the "where" clause
                null,       // columns to group by
                null,       // columns to filter by row groups
                null        // sort order
        );
        contentValues.put(MoviesContract.Favorites._ID, id);
        assertTrue("Error: No Records returned from query", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Query Validation Failed", cursor, contentValues);
        assertFalse("Error: More than one record returned from query", cursor.moveToNext());
    }

    private long insertMovie() {
        MoviesDbHelper helper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createTestMovieValues();

        long id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, testValues);
        if (id == -1) {
            fail("Error by inserting contentValues into database.");
        }

        Cursor cursor = db.query(
                MoviesContract.MovieEntry.TABLE_NAME,
                null,       // all columns
                null,       // Columns for the "where" clause
                null,       // Values for the "where" clause
                null,       // columns to group by
                null,       // columns to filter by row groups
                null        // sort order
        );

        assertTrue("Error: No Records returned from movies query", cursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Failed", cursor, testValues);

        assertFalse("Error: More than one record returned from query", cursor.moveToNext());

        // Test replace on conflict strategy

        ContentValues conflictedValues = TestUtilities.createConflictedMovieValues();
        long conflictedId = db.insertWithOnConflict(MoviesContract.MovieEntry.TABLE_NAME, null, conflictedValues, SQLiteDatabase.CONFLICT_REPLACE);
        if (conflictedId != id) {
            fail("Error by replacing contentValues in database. ID " + conflictedId + " did not match the expected ID " + id);
        }
        cursor = db.query(
                MoviesContract.MovieEntry.TABLE_NAME,
                null,       // all columns
                null,       // Columns for the "where" clause
                null,       // Values for the "where" clause
                null,       // columns to group by
                null,       // columns to filter by row groups
                null        // sort order
        );
        assertTrue("Error: No Records returned from movies query", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Failed", cursor, conflictedValues);
        assertFalse("Error: More than one record returned from location query", cursor.moveToNext());

        cursor.close();
        db.close();

        return id;
    }

    private void checkTableColumns(SQLiteDatabase db, String tableName, String[] tableColumns) {
        Cursor c = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());
        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> columns = new HashSet<String>();
        columns.addAll(Arrays.asList(tableColumns));
        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columns.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The table " + tableName + " doesn't contains all of the required columns",
                columns.isEmpty());
        c.close();
    }
}
