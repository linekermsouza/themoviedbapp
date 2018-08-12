package com.udacity.lineker.themoviedb.detail;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.udacity.lineker.themoviedb.database.AppDatabase;
import com.udacity.lineker.themoviedb.database.MovieEntry;

public class FavoriteDetailViewModel extends ViewModel {

    private LiveData<MovieEntry> movie;

    public FavoriteDetailViewModel(AppDatabase database, int movieId) {
        movie = database.movieDao().loadMovieByIdMovie(movieId);
    }

    public LiveData<MovieEntry> getMovie() {
        return movie;
    }
}
