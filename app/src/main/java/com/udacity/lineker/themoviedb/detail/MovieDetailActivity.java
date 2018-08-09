package com.udacity.lineker.themoviedb.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.udacity.lineker.themoviedb.R;
import com.udacity.lineker.themoviedb.model.Movie;
import com.udacity.lineker.themoviedb.util.ConnectionUtil;

public class MovieDetailActivity extends AppCompatActivity implements GetMovieDetailRequest.GetMovieDetailRequestListener {

    public static final String EXTRA_MOVIE = "movie_data";

    TextView tvTitle;
    TextView tvGenre;
    TextView tvAverage;
    TextView tvVotes;
    TextView tvRuntime;
    TextView tvRelease;
    TextView tvSummary;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        final Movie movie = (Movie) intent.getParcelableExtra(EXTRA_MOVIE);


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

        if (!ConnectionUtil.isOnline( this)) {
            Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
        } else {
            new GetMovieDetailRequest(this, this).execute(movie.getId());
        }
    }

    private void loadBackdrop(Movie movie) {
        final ImageView imageView = findViewById(R.id.backdrop);
        Glide.with(this).load(movie.getPosterPath()).apply(RequestOptions.centerCropTransform()).into(imageView);

        final ImageView imageViewBackground = findViewById(R.id.background);
        Glide.with(this).load(movie.getBackdropPath()).apply(RequestOptions.centerCropTransform()).into(imageViewBackground);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void getMovieDetailRequestCompleted(Movie result) {
        if (result == null) {
            return;
        }
        this.tvTitle.setText(result.getTitle());
        this.tvGenre.setText(result.getGenre());
        this.tvAverage.setText(result.getRate());
        this.tvVotes.setText(result.getVote());
        this.tvRuntime.setText(result.getRuntime());
        this.tvRelease.setText(result.getRelease());
        this.tvSummary.setText(result.getSummary());
    }
}
