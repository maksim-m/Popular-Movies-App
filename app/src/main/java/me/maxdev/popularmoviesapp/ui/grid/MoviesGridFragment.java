package me.maxdev.popularmoviesapp.ui.grid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import javax.inject.Inject;

import me.maxdev.popularmoviesapp.PopularMoviesApp;
import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.data.MoviesService;
import me.maxdev.popularmoviesapp.data.SortHelper;
import me.maxdev.popularmoviesapp.ui.EndlessRecyclerViewOnScrollListener;
import me.maxdev.popularmoviesapp.ui.SortingDialogFragment;

public class MoviesGridFragment extends AbstractMoviesGridFragment {

    @Inject
    MoviesService moviesService;
    @Inject
    SortHelper sortHelper;

    private EndlessRecyclerViewOnScrollListener endlessRecyclerViewOnScrollListener;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MoviesService.BROADCAST_UPDATE_FINISHED)) {
                if (!intent.getBooleanExtra(MoviesService.EXTRA_IS_SUCCESSFUL_UPDATED, true)) {
                    Snackbar.make(swipeRefreshLayout, R.string.error_failed_to_update_movies,
                            Snackbar.LENGTH_LONG)
                            .show();
                }
                swipeRefreshLayout.setRefreshing(false);
                endlessRecyclerViewOnScrollListener.setLoading(false);
                updateGridLayout();
            } else if (action.equals(SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED)) {
                recyclerView.smoothScrollToPosition(0);
                restartLoader();
            }
        }
    };

    public static MoviesGridFragment create() {
        return new MoviesGridFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ((PopularMoviesApp) getActivity().getApplication()).getNetworkComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MoviesService.BROADCAST_UPDATE_FINISHED);
        intentFilter.addAction(SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);
        if (endlessRecyclerViewOnScrollListener != null) {
            endlessRecyclerViewOnScrollListener.setLoading(moviesService.isLoading());
        }
        swipeRefreshLayout.setRefreshing(moviesService.isLoading());
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_movies_grid, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_show_sort_by_dialog:
                showSortByDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    @NonNull
    protected Uri getContentUri() {
        return sortHelper.getSortedMoviesUri();
    }

    @Override
    protected void onCursorLoaded(Cursor data) {
        getAdapter().changeCursor(data);
        if (data == null || data.getCount() == 0) {
            refreshMovies();
        }
    }

    @Override
    protected void onRefreshAction() {
        refreshMovies();
    }

    @Override
    protected void onMoviesGridInitialisationFinished() {
        endlessRecyclerViewOnScrollListener = new EndlessRecyclerViewOnScrollListener(getGridLayoutManager()) {
            @Override
            public void onLoadMore() {
                swipeRefreshLayout.setRefreshing(true);
                moviesService.loadMoreMovies();
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerViewOnScrollListener);
    }

    private void refreshMovies() {
        swipeRefreshLayout.setRefreshing(true);
        moviesService.refreshMovies();
    }

    private void showSortByDialog() {
        DialogFragment sortingDialogFragment = new SortingDialogFragment();
        sortingDialogFragment.show(getFragmentManager(), SortingDialogFragment.TAG);
    }
}
