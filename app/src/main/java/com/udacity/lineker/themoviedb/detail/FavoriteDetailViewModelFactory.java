package com.udacity.lineker.themoviedb.detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import com.udacity.lineker.themoviedb.database.AppDatabase;


public class FavoriteDetailViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mMovieId;

    public FavoriteDetailViewModelFactory(AppDatabase database, int moveiId) {
        mDb = database;
        mMovieId = moveiId;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new FavoriteDetailViewModel(mDb, mMovieId);
    }
}
