package me.maxdev.popularmoviesapp.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

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

        //mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);

        SQLiteDatabase db = new MoviesDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> moviesColumnHashSet = new HashSet<String>();
        moviesColumnHashSet.add(MoviesContract.MovieEntry._ID);
        moviesColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        moviesColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_OVERVIEW);
        moviesColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE);
        moviesColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);
        moviesColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_POPULARITY);
        moviesColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_TITLE);
        moviesColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_AVERAGE_VOTE);
        moviesColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_VOTE_COUNT);
        moviesColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            moviesColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                moviesColumnHashSet.isEmpty());
        db.close();
    }

    public void testMoviesTable() {
        insertMovie();
    }

    public long insertMovie() {
        MoviesDbHelper helper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createTestMovieValues();

        long id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null,testValues);

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

        assertFalse("Error: More than one record returned from location query", cursor.moveToNext());

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
}
