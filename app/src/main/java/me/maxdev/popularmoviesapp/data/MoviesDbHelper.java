package me.maxdev.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for movies.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_SCHEMA_VERSION = 1;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_POPULARITY + " REAL, " +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT, " +
                MoviesContract.MovieEntry.COLUMN_AVERAGE_VOTE + " REAL, " +
                MoviesContract.MovieEntry.COLUMN_VOTE_COUNT + " INTEGER," +
                MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT " +
                " );";
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
