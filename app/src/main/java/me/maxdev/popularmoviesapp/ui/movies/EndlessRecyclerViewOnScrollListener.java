package me.maxdev.popularmoviesapp.ui.movies;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessRecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {

    private static final int VISIBLE_THRESHOLD = 5;

    private GridLayoutManager gridLayoutManager;
    private boolean loading = false;

    public EndlessRecyclerViewOnScrollListener(GridLayoutManager gridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int totalItemCount = gridLayoutManager.getItemCount();
        int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

        boolean endHasBeenReached = firstVisibleItem + VISIBLE_THRESHOLD >= totalItemCount;
        if (!loading && totalItemCount > 0 && endHasBeenReached) {
            loading = true;
            onLoadMore();
        }
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public abstract void onLoadMore();
}
