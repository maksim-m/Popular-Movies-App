package me.maxdev.popularmoviesapp.data;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

import me.maxdev.popularmoviesapp.R;

/**
 * Created by Max on 27.05.2016.
 */
public final class SortingUtil {

    private SortingUtil() {

    }

    public static Uri getSortedMoviesUri(Context context) {
        String sortPref = PreferencesUtility.getSortByPreference(context);
        Resources resources = context.getResources();
        if (sortPref.equals(resources.getString(R.string.pref_sort_by_most_popular))) {
            return MoviesContract.MostPopularMovies.CONTENT_URI;
        } else if (sortPref.equals(resources.getString(R.string.pref_sort_by_highest_rated))) {
            return MoviesContract.HighestRatedMovies.CONTENT_URI;
        } else if (sortPref.equals(resources.getString(R.string.pref_sort_by_most_rated))) {
            return MoviesContract.MostRatedMovies.CONTENT_URI;
        } else {
            return null;
        }
    }
}
