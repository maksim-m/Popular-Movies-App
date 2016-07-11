package me.maxdev.popularmoviesapp.api;

import me.maxdev.popularmoviesapp.data.Movie;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface TheMovieDbService {

    @GET("movie/{id}")
    Observable<Movie> getMovie(@Path("id") long id);

    @GET("movie/{id}/videos")
    Observable<Movie> getMovieVideos(@Path("id") long id);

    @GET("discover/movie")
    Observable<DiscoverResponse<Movie>> discoverMovies(@Query("sort_by") String sortBy, @Query("page") Integer page);

}
