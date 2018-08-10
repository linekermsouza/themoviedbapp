package com.udacity.lineker.themoviedb.detail;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.udacity.lineker.themoviedb.model.Movie;

public class DetailViewModel extends AndroidViewModel {
    private Movie movie;

    public DetailViewModel(@NonNull Application application) {
        super(application);
    }


    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
