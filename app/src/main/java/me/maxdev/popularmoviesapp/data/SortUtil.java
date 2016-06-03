package me.maxdev.popularmoviesapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import me.maxdev.popularmoviesapp.R;

/**
 * Created by Max on 27.05.2016.
 */
public final class SortUtil {

    private SortUtil() {

    }

    public static Sort getSortByPreference(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sort = prefs.getString(
                context.getString(R.string.pref_sort_by_key),
                context.getString(R.string.pref_sort_by_default)
        );
        return Sort.fromString(sort);
    }

    public static Uri getSortedMoviesUri(Context context) {
        Sort sort = getSortByPreference(context);
        switch (sort) {
            case MOST_POPULAR:
                return MoviesContract.MostPopularMovies.CONTENT_URI;
            case HIGHEST_RATED:
                return MoviesContract.HighestRatedMovies.CONTENT_URI;
            case MOST_RATED:
                return MoviesContract.MostRatedMovies.CONTENT_URI;
            default:
                return null;
        }
    }

    public static void saveSortByPreference(Context context, Sort sort) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(
                context.getString(R.string.pref_sort_by_key),
                sort.toString()
        );
        editor.apply();
    }
}
