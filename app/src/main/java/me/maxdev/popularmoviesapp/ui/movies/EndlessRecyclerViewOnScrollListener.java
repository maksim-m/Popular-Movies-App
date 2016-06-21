package me.maxdev.popularmoviesapp.ui.movies;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class EndlessRecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {

    private static final String LOG_TAG = "EndlessRecyclerViewOnScrollListener";
    private static final int VISIBLE_THRESHOLD = 5;

    private GridLayoutManager gridLayoutManager;
    private boolean loading = false;

    public EndlessRecyclerViewOnScrollListener(GridLayoutManager gridLayoutManager) {
        this.gridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        //int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = gridLayoutManager.getItemCount();
        int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

        boolean endHasBeenReached = firstVisibleItem + VISIBLE_THRESHOLD >= totalItemCount;
        if (!loading && totalItemCount > 0 && endHasBeenReached) {
            Log.d("xxx", "endHasBeenReached");
            loading = true;
            onLoadMore();
        }
    }

    public void onLoadingDone() {
        loading = false;
    }

    public abstract void onLoadMore();
}
