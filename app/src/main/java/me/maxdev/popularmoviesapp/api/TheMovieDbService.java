package me.maxdev.popularmoviesapp.api;

import me.maxdev.popularmoviesapp.data.Movie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface TheMovieDbService {

    @GET("movie/{id}")
    Call<Movie> getMovie(@Path("id") long id);

    @GET("discover/movie")
    Call<DiscoverResponse<Movie>> discoverMovies(@Query("sort_by") String sortBy);

}
