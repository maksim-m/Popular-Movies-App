package me.maxdev.popularmoviesapp.ui.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.maxdev.popularmoviesapp.PopularMoviesApp;
import me.maxdev.popularmoviesapp.R;
import me.maxdev.popularmoviesapp.api.MovieReviewsResponse;
import me.maxdev.popularmoviesapp.api.MovieVideosResponse;
import me.maxdev.popularmoviesapp.api.TheMovieDbService;
import me.maxdev.popularmoviesapp.data.Movie;
import me.maxdev.popularmoviesapp.data.MovieReview;
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
    private static final String MOVIE_REVIEWS_KEY = "MovieReviews";
    private static final String LOG_TAG = "MovieDetailFragment";

    private static final double VOTE_PERFECT = 9.0;
    private static final double VOTE_GOOD = 7.0;
    private static final double VOTE_NORMAL = 5.0;

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
    @BindView(R.id.card_movie_detail)
    CardView cardMovieDetail;
    @BindView(R.id.card_movie_overview)
    CardView cardMovieOverview;

    @BindView(R.id.card_movie_videos)
    CardView cardMovieVideos;
    @BindView(R.id.movie_videos)
    RecyclerView movieVideos;

    @BindView(R.id.card_movie_reviews)
    CardView cardMovieReviews;
    @BindView(R.id.movie_reviews)
    RecyclerView movieReviews;

    @Inject
    TheMovieDbService theMovieDbService;

    private Movie movie;
    private MovieVideosAdapter videosAdapter;
    private MovieReviewsAdapter reviewsAdapter;

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

        ((PopularMoviesApp) getActivity().getApplication()).getNetworkComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);
        initViews();
        initVideosList();
        initReviewsList();
        setupCardsElevation();
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (videosAdapter.getItemCount() == 0) {
            loadMovieVideos();
        }
        if (reviewsAdapter.getItemCount() == 0) {
            loadMovieReviews();
        }
        updateMovieVideosCard();
        updateMovieReviewsCard();
    }


    private void setupCardsElevation() {
        setupCardElevation(cardMovieDetail);
        setupCardElevation(cardMovieVideos);
        setupCardElevation(cardMovieOverview);
        setupCardElevation(cardMovieReviews);
    }

    private void updateMovieVideosCard() {
        if (videosAdapter == null || videosAdapter.getItemCount() == 0) {
            cardMovieVideos.setVisibility(View.GONE);
        } else {
            cardMovieVideos.setVisibility(View.VISIBLE);
        }
    }

    private void updateMovieReviewsCard() {
        if (reviewsAdapter == null || reviewsAdapter.getItemCount() == 0) {
            cardMovieReviews.setVisibility(View.GONE);
        } else {
            cardMovieReviews.setVisibility(View.VISIBLE);
        }
    }

    private void setupCardElevation(View view) {
        ViewCompat.setElevation(view,
                convertDpToPixel(getResources().getInteger(R.integer.movie_detail_content_elevation_in_dp)));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (videosAdapter.getItemCount() != 0) {
            outState.putParcelableArrayList(MOVIE_VIDEOS_KEY, videosAdapter.getMovieVideos());
        }
        if (reviewsAdapter.getItemCount() != 0) {
            outState.putParcelableArrayList(MOVIE_REVIEWS_KEY, reviewsAdapter.getMovieReviews());
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            videosAdapter.setMovieVideos(savedInstanceState.getParcelableArrayList(MOVIE_VIDEOS_KEY));
            reviewsAdapter.setMovieReviews(savedInstanceState.getParcelableArrayList(MOVIE_REVIEWS_KEY));
        }
    }

    private void loadMovieVideos() {
        theMovieDbService.getMovieVideos(movie.getId())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .map(MovieVideosResponse::getResults)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<MovieVideo>>() {
                    @Override
                    public void onCompleted() {
                        // do nothing
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, e.getMessage());
                        updateMovieVideosCard();
                    }

                    @Override
                    public void onNext(ArrayList<MovieVideo> movieVideos) {
                        videosAdapter.setMovieVideos(movieVideos);
                        updateMovieVideosCard();
                    }
                });
    }

    private void loadMovieReviews() {
        theMovieDbService.getMovieReviews(movie.getId())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.newThread())
                .map(MovieReviewsResponse::getResults)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<MovieReview>>() {
                    @Override
                    public void onCompleted() {
                        // do nothing
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LOG_TAG, e.getMessage());
                        updateMovieReviewsCard();
                    }

                    @Override
                    public void onNext(ArrayList<MovieReview> movieReviews) {
                        reviewsAdapter.setMovieReviews(movieReviews);
                        updateMovieReviewsCard();
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
        movieUserRating.setText(String.format(Locale.US, "%.1f", movie.getAverageVote()));
        movieUserRating.setTextColor(getRatingColor(movie.getAverageVote()));
        String releaseDate = String.format(getString(me.maxdev.popularmoviesapp.R.string.movie_detail_release_date),
                movie.getReleaseDate());
        movieReleaseDate.setText(releaseDate);
        movieOverview.setText(movie.getOverview());
    }

    @ColorInt
    private int getRatingColor(double averageVote) {
        if (averageVote >= VOTE_PERFECT) {
            return ContextCompat.getColor(getContext(), R.color.vote_perfect);
        } else if (averageVote >= VOTE_GOOD) {
            return ContextCompat.getColor(getContext(), R.color.vote_good);
        } else if (averageVote >= VOTE_NORMAL) {
            return ContextCompat.getColor(getContext(), R.color.vote_normal);
        } else {
            return ContextCompat.getColor(getContext(), R.color.vote_bad);
        }
    }

    private void initVideosList() {
        videosAdapter = new MovieVideosAdapter(getContext());
        videosAdapter.setOnItemClickListener((itemView, position) -> onMovieVideoClicked(position));
        movieVideos.setAdapter(videosAdapter);
        movieVideos.setItemAnimator(new DefaultItemAnimator());
        movieVideos.addItemDecoration(new ItemOffsetDecoration(getActivity(), R.dimen.movie_item_offset));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        movieVideos.setLayoutManager(layoutManager);
    }

    private void initReviewsList() {
        reviewsAdapter = new MovieReviewsAdapter();
        reviewsAdapter.setOnItemClickListener((itemView, position) -> onMovieReviewClicked(position));
        movieReviews.setAdapter(reviewsAdapter);
        movieReviews.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        movieReviews.setLayoutManager(layoutManager);
    }

    private void onMovieReviewClicked(int position) {
        MovieReview review = reviewsAdapter.getItem(position);
        if (review != null && review.getReviewUrl() != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getReviewUrl()));
            startActivity(intent);
        }
    }

    private void onMovieVideoClicked(int position) {
        MovieVideo video = videosAdapter.getItem(position);
        if (video != null && video.isYoutubeVideo()) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + video.getKey()));
            startActivity(intent);
        }
    }

    public float convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}
