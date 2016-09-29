package me.maxdev.popularmoviesapp;

import javax.inject.Singleton;

import dagger.Component;
import me.maxdev.popularmoviesapp.api.NetworkModule;
import me.maxdev.popularmoviesapp.ui.MainActivity;
import me.maxdev.popularmoviesapp.ui.SortingDialogFragment;
import me.maxdev.popularmoviesapp.ui.detail.MovieDetailActivity;
import me.maxdev.popularmoviesapp.ui.detail.MovieDetailFragment;
import me.maxdev.popularmoviesapp.ui.grid.MoviesGridFragment;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface NetworkComponent {

    void inject(MoviesGridFragment moviesGridFragment);

    void inject(MainActivity mainActivity);

    void inject(SortingDialogFragment sortingDialogFragment);

    void inject(MovieDetailActivity movieDetailActivity);

    void inject(MovieDetailFragment movieDetailFragment);

}
