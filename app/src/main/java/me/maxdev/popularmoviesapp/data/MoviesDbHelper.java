package me.maxdev.popularmoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for movies.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_SCHEMA_VERSION = 4;
    private static final String SQL_DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS ";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MoviesContract.MovieEntry.SQL_CREATE_TABLE);
        db.execSQL(MoviesContract.MostPopularMovies.SQL_CREATE_TABLE);
        db.execSQL(MoviesContract.HighestRatedMovies.SQL_CREATE_TABLE);
        db.execSQL(MoviesContract.MostRatedMovies.SQL_CREATE_TABLE);
        db.execSQL(MoviesContract.Favorites.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE_IF_EXISTS + MoviesContract.MovieEntry.TABLE_NAME);
        db.execSQL(SQL_DROP_TABLE_IF_EXISTS + MoviesContract.MostPopularMovies.TABLE_NAME);
        db.execSQL(SQL_DROP_TABLE_IF_EXISTS + MoviesContract.HighestRatedMovies.TABLE_NAME);
        db.execSQL(SQL_DROP_TABLE_IF_EXISTS + MoviesContract.MostRatedMovies.TABLE_NAME);
        db.execSQL(SQL_DROP_TABLE_IF_EXISTS + MoviesContract.Favorites.TABLE_NAME);
        onCreate(db);
    }
}
