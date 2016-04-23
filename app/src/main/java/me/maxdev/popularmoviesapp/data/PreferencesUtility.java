package me.maxdev.popularmoviesapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.List;

import me.maxdev.popularmoviesapp.R;

public class PreferencesUtility {

    public static String getSortByPreference(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(
                context.getString(R.string.pref_sort_by_key),
                context.getString(R.string.pref_sort_by_default)
        );
    }

    public static int getSortByPreferenceIndex(Context context) {
        String sortByPreference = PreferencesUtility.getSortByPreference(context);
        List<String> sortByOptionsLabels = Arrays.asList(
                context.getResources().getStringArray(R.array.pref_sort_by_values));
        return sortByOptionsLabels.indexOf(sortByPreference);
    }

    public static void saveSortByPreference(Context context, int index) {
        String[] sortByPreferencesValues = context.getResources().getStringArray(R.array.pref_sort_by_values);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(
                context.getString(R.string.pref_sort_by_key),
                sortByPreferencesValues[index]
        );
        editor.commit();
    }

    private PreferencesUtility() { }
}
