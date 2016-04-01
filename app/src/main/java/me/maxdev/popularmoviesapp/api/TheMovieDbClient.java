package me.maxdev.popularmoviesapp.api;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TheMovieDbClient {

    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static final String CACHE_DIR = "HttpResponseCache";
    private static final int CONNECT_TIMEOUT = 15;
    private static final int WRITE_TIMEOUT = 60;
    private static final int TIMEOUT = 60;

    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(new LoggingInterceptor())
            .addInterceptor(new AuthorizationInterceptor())
            .build();

    private static TheMovieDbService theMovieDbService;

    public static TheMovieDbService getTheMovieDbService(Context context) {

        if (theMovieDbService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(CLIENT)
                    .build();
            theMovieDbService = retrofit.create(TheMovieDbService.class);
        }

        return theMovieDbService;
    }


}
