package me.maxdev.popularmoviesapp.ui.movies;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.data.MoviesService;
import me.maxdev.popularmoviesapp.data.SortUtil;
import me.maxdev.popularmoviesapp.ui.ItemOffsetDecoration;
import me.maxdev.popularmoviesapp.ui.movies.detail.MovieDetailsActivity;
import me.maxdev.popularmoviesapp.util.OnItemClickListener;

public class MoviesGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG = "MoviesGridFragment";
    private static final int LOADER_ID = 0;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.movies_grid)
    RecyclerView recyclerView;

    private MoviesService moviesService;
    private Uri contentUri;
    private MoviesAdapter adapter;
    private EndlessRecyclerViewOnScrollListener endlessRecyclerViewOnScrollListener;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MoviesService.BROADCAST_UPDATE_FINISHED)) {
                if (!intent.getBooleanExtra(MoviesService.EXTRA_IS_SUCCESSFUL_UPDATED, true)) {
                    Snackbar.make(swipeRefreshLayout, R.string.snackbar_failed_to_update_movies,
                            Snackbar.LENGTH_LONG)
                            .show();
                }
                swipeRefreshLayout.setRefreshing(false);
            } else if (action.equals(SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED)) {
                contentUri = SortUtil.getSortedMoviesUri(getContext());
                recyclerView.smoothScrollToPosition(0);
                getLoaderManager().restartLoader(LOADER_ID, null, MoviesGridFragment.this);
            }
        }
    };

    public MoviesGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        moviesService = MoviesService.getInstance(getContext());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        contentUri = SortUtil.getSortedMoviesUri(getContext());
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MoviesService.BROADCAST_UPDATE_FINISHED);
        intentFilter.addAction(SortingDialogFragment.BROADCAST_SORT_PREFERENCE_CHANGED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
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

    private void showSortByDialog() {
        DialogFragment sortingDialogFragment = new SortingDialogFragment();
        sortingDialogFragment.show(getFragmentManager(), SortingDialogFragment.TAG);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        ButterKnife.bind(this, rootView);

        initSwipeRefreshLayout();
        initMoviesGrid();

        return rootView;
    }

    private void initMoviesGrid() {
        adapter = new MoviesAdapter(getContext(), null);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int columns = getResources().getInteger(R.integer.movies_columns);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.movie_item_offset));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), columns);
        recyclerView.setLayoutManager(gridLayoutManager);
        endlessRecyclerViewOnScrollListener = new EndlessRecyclerViewOnScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore() {
                Log.e("xxx", "Start loading more.");
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("xxx", "Loaded.");
                        endlessRecyclerViewOnScrollListener.onLoadingDone();
                    }
                }, 3000);
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerViewOnScrollListener);
    }

    @SuppressLint("PrivateResource")
    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_material_dark,
                R.color.accent_material_light);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_movies_grid, menu);
    }

    private void refreshMovies() {
        swipeRefreshLayout.setRefreshing(true);
        moviesService.refreshMovies();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                contentUri,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);
        if (data == null || data.getCount() == 0) {
            refreshMovies();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    @Override
    public void onItemClick(View itemView, int position) {
        Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
        intent.putExtra("movieId", adapter.getItemId(position));
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        refreshMovies();
    }
}
