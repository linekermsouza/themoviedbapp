package com.udacity.lineker.themoviedb.util;

import com.udacity.lineker.themoviedb.database.MovieEntry;
import com.udacity.lineker.themoviedb.model.Movie;

public class ModelUtil {
    public static final Movie movieEntryToMovie(MovieEntry movieEntry) {
        Movie movie = new Movie();
        movie.setId(movieEntry.getIdMovie());
        movie.setSummary(movieEntry.getSummary());
        movie.setRelease(movieEntry.getRelease());
        movie.setRuntime(movieEntry.getRuntime());
        movie.setVote(movieEntry.getVote());
        movie.setGenre(movieEntry.getGenre());
        movie.setTitle(movieEntry.getTitle());
        movie.setBackdropPath(movieEntry.getBackdropPath());
        movie.setPosterPath(movieEntry.getPosterPath());
        movie.setRate(movieEntry.getRate());
        return movie;
    }

    public static final MovieEntry movieToMovieEntry(Movie movie) {
        MovieEntry movieEntry = new MovieEntry();
        movieEntry.setIdMovie(movie.getId());
        movieEntry.setSummary(movie.getSummary());
        movieEntry.setRelease(movie.getRelease());
        movieEntry.setRuntime(movie.getRuntime());
        movieEntry.setVote(movie.getVote());
        movieEntry.setGenre(movie.getGenre());
        movieEntry.setTitle(movie.getTitle());
        movieEntry.setBackdropPath(movie.getBackdropPath());
        movieEntry.setPosterPath(movie.getPosterPath());
        movieEntry.setRate(movie.getRate());
        return movieEntry;
    }
}
