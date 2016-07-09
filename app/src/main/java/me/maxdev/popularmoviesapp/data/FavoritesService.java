package me.maxdev.popularmoviesapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class FavoritesService {

    private static volatile FavoritesService instance = null;

    private final Context context;

    public FavoritesService(Context context) {
        if (instance != null) {
            throw new IllegalStateException("Already instantiated.");
        }
        this.context = context.getApplicationContext();
    }

    public static FavoritesService getInstance(Context context) {
        synchronized (FavoritesService.class) {
            if (instance == null) {
                instance = new FavoritesService(context);
            }
        }
        return instance;
    }

    public void addToFavorites(Movie movie) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MoviesContract.COLUMN_MOVIE_ID_KEY, movie.getId());
        context.getContentResolver().insert(MoviesContract.Favorites.CONTENT_URI, contentValues);
    }

    public void removeFromFavorites(Movie movie) {
        context.getContentResolver().delete(
                MoviesContract.Favorites.CONTENT_URI,
                MoviesContract.COLUMN_MOVIE_ID_KEY + " = " + movie.getId(),
                null
        );
    }

    public boolean isFavorite(Movie movie) {
        boolean favorite = false;
        Cursor cursor = context.getContentResolver().query(
                MoviesContract.Favorites.CONTENT_URI,
                null,
                MoviesContract.COLUMN_MOVIE_ID_KEY + " = " + movie.getId(),
                null,
                null
        );
        if (cursor != null) {
            favorite = cursor.getCount() != 0;
            cursor.close();
        }
        return favorite;
    }
}
