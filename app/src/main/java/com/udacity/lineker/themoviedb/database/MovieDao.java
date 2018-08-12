package com.udacity.lineker.themoviedb.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movie ORDER BY id")
    LiveData<List<MovieEntry>> loadAllMovies();

    @Query("SELECT * FROM movie WHERE isFavorite = 1 ORDER BY id")
    LiveData<List<MovieEntry>> loadAllFavorites();

    @Insert
    void insertMovie(MovieEntry movieEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieEntry movieEntry);

    @Delete
    void deleteMovie(MovieEntry movieEntry);

    @Query("SELECT * FROM movie WHERE id = :id")
    LiveData<MovieEntry> loadMovieById(int id);

    @Query("SELECT * FROM movie WHERE idMovie = :idMovie")
    LiveData<MovieEntry> loadMovieByIdMovie(int idMovie);
}
