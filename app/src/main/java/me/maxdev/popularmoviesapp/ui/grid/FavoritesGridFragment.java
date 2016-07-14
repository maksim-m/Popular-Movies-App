package me.maxdev.popularmoviesapp.ui.grid;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import me.maxdev.popularmoviesapp.data.MoviesContract;

public class FavoritesGridFragment extends AbstractMoviesGridFragment {

    public static FavoritesGridFragment create() {
        return new FavoritesGridFragment();
    }

    @Override
    @NonNull
    protected Uri getContentUri() {
        return MoviesContract.Favorites.CONTENT_URI;
    }

    @Override
    protected void onCursorLoaded(Cursor data) {
        getAdapter().changeCursor(data);
    }

    @Override
    protected void onRefreshAction() {
        // do nothing
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onMoviesGridInitialisationFinished() {
        // do nothing
    }
}
