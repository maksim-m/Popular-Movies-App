package me.maxdev.popularmoviesapp.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import me.maxdev.popularmoviesapp.data.MovieVideo;

public class MovieVideosResponse {
    @SerializedName("id")
    private long movieId;

    @SerializedName("results")
    private ArrayList<MovieVideo> results;

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public ArrayList<MovieVideo> getResults() {
        return results;
    }

    public void setResults(ArrayList<MovieVideo> results) {
        this.results = results;
    }
}
