package me.maxdev.popularmoviesapp.data;

import android.support.annotation.NonNull;

public enum Sort {

    MOST_POPULAR("popularity.desc"),
    HIGHEST_RATED("vote_average.desc"),
    MOST_RATED("vote_count.desc");

    private String value;

    Sort(String sort) {
        value = sort;
    }

    public static Sort fromString(@NonNull String string) {
        for (Sort sort : Sort.values()) {
            if (string.equals(sort.toString())) {
                return sort;
            }
        }
        throw new IllegalArgumentException("No constant with text " + string + " found.");
    }

    @Override
    public String toString() {
        return value;
    }

}
