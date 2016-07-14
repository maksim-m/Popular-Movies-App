package me.maxdev.popularmoviesapp.ui.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trello.rxlifecycle.components.support.RxFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.api.TheMovieDbClient;
import me.maxdev.popularmoviesapp.api.TheMovieDbService;
import me.maxdev.popularmoviesapp.data.Movie;
import me.maxdev.popularmoviesapp.data.MovieVideo;
import me.maxdev.popularmoviesapp.ui.ItemOffsetDecoration;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieDetailFragment extends RxFragment {

    private static final String POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String POSTER_IMAGE_SIZE = "w780";

    private static final String ARG_MOVIE = "ArgMovie";
    private static final String MOVIE_VIDEOS_KEY = "MovieVideos";
    private static final String LOG_TAG = "MovieDetailFragment";

    @BindView(R.id.image_movie_detail_poster)
    ImageView movieImagePoster;
    @BindView(R.id.text_movie_original_title)
    TextView movieOriginalTitle;
    @BindView(R.id.text_movie_user_rating)
    TextView movieUserRating;
    @BindView(R.id.text_movie_release_date)
    TextView movieReleaseDate;
    @BindView(R.id.text_movie_overview)
    TextView movieOverview;
    @BindView(R.id.movie_videos)
    RecyclerView movieVideos;

    private Movie movie;
    private MovieVideosAdapter adapter;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    public static MovieDetailFragment create(Movie movie) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movie = getArguments().getParcelable(ARG_MOVIE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);
        initViews();
        initVideosList();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter.getItemCount() == 0) {
            loadMovieVideos();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter.getItemCount() != 0) {
            outState.putParcelableArrayList(MOVIE_VIDEOS_KEY, adapter.getMovieVideos());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            adapter.setMovieVideos(savedInstanceState.getParcelableArrayList(MOVIE_VIDEOS_KEY));
        }
    }

    private void loadMovieVideos() {
        TheMovieDbService service = TheMovieDbClient.getInstance(getContext());
        service.getMovieVideos(movie.getId())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .map(movieVideosResponse -> movieVideosResponse.getResults())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<MovieVideo>>() {
                    @Override
                    public void onCompleted() {
                        // do nothing
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<MovieVideo> movieVideos) {
                        adapter.setMovieVideos(movieVideos);
                    }
                });
    }

    private void initViews() {
        Glide.with(this)
                .load(POSTER_IMAGE_BASE_URL + POSTER_IMAGE_SIZE + movie.getPosterPath())
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(movieImagePoster);
        movieOriginalTitle.setText(movie.getOriginalTitle());
        String userRating = String.format(getString(me.maxdev.popularmoviesapp.R.string.movie_detail_user_rating),
                movie.getAverageVote());
        movieUserRating.setText(userRating);
        String releaseDate = String.format(getString(me.maxdev.popularmoviesapp.R.string.movie_detail_release_date),
                movie.getReleaseDate());
        movieReleaseDate.setText(releaseDate);
        movieOverview.setText(movie.getOverview());
    }

    private void initVideosList() {
        adapter = new MovieVideosAdapter(getContext());
        movieVideos.setAdapter(adapter);
        movieVideos.setItemAnimator(new DefaultItemAnimator());
        movieVideos.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.movie_item_offset));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        movieVideos.setLayoutManager(layoutManager);
    }

}
