package me.maxdev.popularmoviesapp;

import android.app.Application;

import me.maxdev.popularmoviesapp.api.NetworkModule;

public class PopularMoviesApp extends Application {

    private NetworkComponent networkComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        networkComponent = DaggerNetworkComponent.builder()
                .appModule(new AppModule(this))
                .networkModule(new NetworkModule())
                .build();
    }

    public NetworkComponent getNetworkComponent() {
        return networkComponent;
    }

}
