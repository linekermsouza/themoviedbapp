package com.udacity.lineker.themoviedb.detail;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.udacity.lineker.themoviedb.R;
import com.udacity.lineker.themoviedb.database.AppDatabase;
import com.udacity.lineker.themoviedb.database.AppExecutors;
import com.udacity.lineker.themoviedb.database.MovieEntry;
import com.udacity.lineker.themoviedb.main.GetMoviesRequest;
import com.udacity.lineker.themoviedb.model.Movie;
import com.udacity.lineker.themoviedb.model.Review;
import com.udacity.lineker.themoviedb.model.Trailer;
import com.udacity.lineker.themoviedb.util.ConnectionUtil;
import com.udacity.lineker.themoviedb.util.ModelUtil;

import java.util.List;


public class MovieDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Movie> {

    public static final String EXTRA_MOVIE = "movie_data";

    private TextView tvTitle;
    private TextView tvGenre;
    private TextView tvAverage;
    private TextView tvVotes;
    private TextView tvRuntime;
    private TextView tvRelease;
    private TextView tvSummary;
    private View noDataViewTrailers;
    private View noDataViewReviews;
    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private TrailerRecyclerViewAdapter mAdapterTrailers;
    private ReviewRecyclerViewAdapter mAdapterReviews;

    private AppDatabase mDb;
    private Menu menu;
    private boolean isFavorite;
    private MovieEntry movieEntry;
    private Movie movie;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDb = AppDatabase.getInstance(getApplicationContext());

        Intent intent = getIntent();
        final Movie movie = (Movie) intent.getParcelableExtra(EXTRA_MOVIE);
        this.movie = movie;

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getResources().getString(R.string.detail));

        loadBackdrop(movie);

        this.tvTitle = findViewById(R.id.tv_title);
        this.tvGenre = findViewById(R.id.tv_genre);
        this.tvAverage = findViewById(R.id.tv_average);
        this.tvVotes = findViewById(R.id.tv_votes);
        this.tvRuntime = findViewById(R.id.tv_runtime);
        this.tvRelease = findViewById(R.id.tv_release);
        this.tvSummary = findViewById(R.id.tv_summary);
        this.noDataViewTrailers = findViewById(R.id.no_data_trailers);
        this.noDataViewReviews = findViewById(R.id.no_data_reviews);
        this.recyclerViewTrailers = findViewById(R.id.recyclerviewtrailers);
        this.recyclerViewReviews = findViewById(R.id.recyclerviewreviews);
        setupRecyclerViewTrailer(null);
        setupRecyclerViewReview(null);

        FavoriteDetailViewModelFactory factory = new FavoriteDetailViewModelFactory(mDb, movie.getId());
                final FavoriteDetailViewModel viewModel
                        = ViewModelProviders.of(this, factory).get(FavoriteDetailViewModel.class);

                viewModel.getMovie().observe(this, new Observer<MovieEntry>() {
                    @Override
                    public void onChanged(@Nullable MovieEntry movieEntry) {
                        MovieDetailActivity.this.movieEntry = movieEntry;
                        viewModel.getMovie().removeObserver(this);
                        MovieDetailActivity.this.isFavorite = movieEntry == null ? false : movieEntry.isFavorite();
                        updateFavoriteIcon();
                    }
                });

        updateUiFields(movie);

        if (!ConnectionUtil.isOnline( this)) {
            Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
        } else {
            updateData(movie);
        }
    }

    private void setupRecyclerViewTrailer(List<Trailer> trailers) {
        noDataViewTrailers.setVisibility(trailers != null && trailers.size() == 0 ? View.VISIBLE : View.GONE);
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapterTrailers = new TrailerRecyclerViewAdapter(this,
                trailers);
        recyclerViewTrailers.setAdapter(mAdapterTrailers);
    }

    private void setupRecyclerViewReview(List<Review> reviews) {
        noDataViewReviews.setVisibility(reviews != null && reviews.size() == 0 ? View.VISIBLE : View.GONE);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapterReviews = new ReviewRecyclerViewAdapter(this,
                reviews);
        recyclerViewReviews.setAdapter(mAdapterReviews);
    }

    private void updateData(Movie movie) {
        Bundle queryBundle = new Bundle();
        queryBundle.putInt(GetMovieDetailRequest.REQUEST_MOVIE_ID_EXTRA, movie.getId());
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> getMovieDetailLoader = loaderManager.getLoader(GetMoviesRequest.REQUEST_MOVIES_LOADER);
        if (getMovieDetailLoader == null) {
            loaderManager.initLoader(GetMovieDetailRequest.REQUEST_MOVIE_DETAIL_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(GetMovieDetailRequest.REQUEST_MOVIE_DETAIL_LOADER, queryBundle, this);
        }
    }

    private void loadBackdrop(Movie movie) {
        final ImageView imageView = findViewById(R.id.backdrop);
        Glide.with(this).load(movie.getPosterPath()).apply(RequestOptions.centerCropTransform()).into(imageView);

        final ImageView imageViewBackground = findViewById(R.id.background);
        Glide.with(this).load(movie.getBackdropPath()).apply(RequestOptions.centerCropTransform()).into(imageViewBackground);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_detail, menu);
        updateFavoriteIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            case R.id.action_favorite:
                this.isFavorite = !this.isFavorite;
                updateFavoriteIcon();

                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (movieEntry == null) {
                            MovieEntry movieEntry = ModelUtil.movieToMovieEntry(MovieDetailActivity.this.movie);
                            movieEntry.setFavorite(MovieDetailActivity.this.isFavorite);
                            mDb.movieDao().insertMovie(movieEntry);
                        } else if (movieEntry != null) {
                            movieEntry.setFavorite(MovieDetailActivity.this.isFavorite);
                            mDb.movieDao().updateMovie(movieEntry);
                        }
                    }
                });
                break;
        }
        return true;
    }

    private void updateFavoriteIcon() {
        if (menu == null) {
            return;
        }
        if (this.isFavorite) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, android.R.drawable.star_big_on));
        } else {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, android.R.drawable.star_big_off));
        }
    }

    private void updateUiFields(Movie movie) {
        this.tvTitle.setText(movie.getTitle());
        this.tvGenre.setText(movie.getGenre());
        this.tvAverage.setText(movie.getRate());
        this.tvVotes.setText(movie.getVote());
        this.tvRuntime.setText(movie.getRuntime());
        this.tvRelease.setText(movie.getRelease());
        this.tvSummary.setText(movie.getSummary());

        noDataViewTrailers.setVisibility(movie.getTrailers() != null && movie.getTrailers().size() == 0 ? View.VISIBLE : View.GONE);
        mAdapterTrailers.setTrailers(movie.getTrailers());

        noDataViewReviews.setVisibility(movie.getReviews() != null && movie.getReviews().size() == 0 ? View.VISIBLE : View.GONE);
        mAdapterReviews.setReviews(movie.getReviews());

    }

    @NonNull
    @Override
    public Loader<Movie> onCreateLoader(int id, @Nullable Bundle args) {
        DetailViewModel viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        return new GetMovieDetailRequest(this, args, viewModel.getMovie());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Movie> loader, Movie data) {
        if (data == null) {
            return;
        }
        updateViewModel(data);
        updateUiFields(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Movie> loader) {

    }

    private void updateViewModel(Movie movie) {
        DetailViewModel viewModel = ViewModelProviders.of(this).get(DetailViewModel.class);
        viewModel.setMovie(movie);
    }
}
