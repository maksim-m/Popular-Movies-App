package me.maxdev.popularmoviesapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movies database.
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "me.maxdev.popularmoviesapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    private MoviesContract() { }

    /* Inner class that defines the contents of the movies table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_DIR_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AVERAGE_VOTE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";

        public static final String[] COLUMNS = {_ID, COLUMN_ORIGINAL_TITLE, COLUMN_OVERVIEW,
                COLUMN_RELEASE_DATE, COLUMN_POSTER_PATH, COLUMN_POPULARITY, COLUMN_TITLE,
                COLUMN_AVERAGE_VOTE, COLUMN_VOTE_COUNT, COLUMN_BACKDROP_PATH};

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +
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

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }

        private MovieEntry() { }
    }
}
