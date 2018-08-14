package com.udacity.lineker.themoviedb.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.udacity.lineker.themoviedb.model.Movie;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static List<Movie> movies;
    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
