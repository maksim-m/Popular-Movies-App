package me.maxdev.popularmoviesapp.api;

import me.maxdev.popularmoviesapp.data.Movie;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface TheMovieDbService {

    @GET("movie/{id}/videos")
    Observable<MovieVideosResponse> getMovieVideos(@Path("id") long movieId);

    @GET("movie/{id}/reviews")
    Observable<MovieReviewsResponse> getMovieReviews(@Path("id") long movieId);

    @GET("discover/movie")
    Observable<DiscoverAndSearchResponse<Movie>> discoverMovies(@Query("sort_by") String sortBy,
                                                                @Query("page") Integer page);

    @GET("search/movie")
    Observable<DiscoverAndSearchResponse<Movie>> searchMovies(@Query("query") String query,
                                                              @Query("page") Integer page);

}
