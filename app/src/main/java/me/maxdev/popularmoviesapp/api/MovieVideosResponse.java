package me.maxdev.popularmoviesapp.api;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import me.maxdev.popularmoviesapp.data.MovieVideo;

public class MovieVideosResponse {
    @SerializedName("id")
    private long movieId;

    @SerializedName("results")
    private ArrayList<MovieVideo> results;

    public MovieVideosResponse(long movieId, ArrayList<MovieVideo> results) {
        this.movieId = movieId;
        this.results = results;
    }

    public long getMovieId() {
        return movieId;
    }

    public ArrayList<MovieVideo> getResults() {
        return results;
    }
}
