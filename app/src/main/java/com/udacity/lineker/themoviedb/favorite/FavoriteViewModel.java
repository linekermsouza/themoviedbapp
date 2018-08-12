package com.udacity.lineker.themoviedb.favorite;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.udacity.lineker.themoviedb.database.AppDatabase;
import com.udacity.lineker.themoviedb.database.MovieEntry;

import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = FavoriteViewModel.class.getSimpleName();

    private LiveData<List<MovieEntry>> movies;

    public FavoriteViewModel(Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the movies from the DataBase");
        movies = database.movieDao().loadAllFavorites();
    }

    public LiveData<List<MovieEntry>> getMovies() {
        return movies;
    }
}
