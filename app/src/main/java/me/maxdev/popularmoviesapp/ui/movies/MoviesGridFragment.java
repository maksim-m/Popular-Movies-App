package me.maxdev.popularmoviesapp.ui.movies;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.data.MoviesContract;
import me.maxdev.popularmoviesapp.data.MoviesService;
import me.maxdev.popularmoviesapp.ui.ItemOffsetDecoration;
import me.maxdev.popularmoviesapp.ui.movies.detail.MovieDetailsActivity;
import me.maxdev.popularmoviesapp.util.OnItemClickListener;


public class MoviesGridFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, OnItemClickListener {

    private static final String LOG_TAG = "MoviesGridFragment";
    private static final int LOADER_ID = 0;
    private MoviesAdapter adapter;
    private RecyclerView recyclerView;

    public MoviesGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        //updateMovies();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                updateMovies();
                return true;
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
        recyclerView = (RecyclerView) rootView.findViewById(R.id.movies_grid);

        initMoviesGrid();

        return rootView;
    }

    private void initMoviesGrid() {
        adapter = new MoviesAdapter(getActivity(), null);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        int columns = getResources().getInteger(R.integer.movies_columns);
        recyclerView.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.movie_item_offset));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columns));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviesgridfragment, menu);
    }

    private void updateMovies() {
        Intent intent = new Intent(getActivity(), MoviesService.class);
        getActivity().startService(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                MoviesContract.MovieEntry.COLUMN_POPULARITY + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);
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
}
