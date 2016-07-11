package me.maxdev.popularmoviesapp.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.maxdev.popularmoviesapp.data.Video;

public class VideosResponse {
    @SerializedName("id")
    private long movieId;

    @SerializedName("results")
    private List<Video> results;

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }
}
