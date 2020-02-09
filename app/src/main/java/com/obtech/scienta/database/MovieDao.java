package com.obtech.scienta.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.obtech.scienta.model.Movie.Movie;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movie")
    LiveData<List<com.obtech.scienta.model.Movie.Movie>> getAllMovies();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMovie(com.obtech.scienta.model.Movie.Movie movie);

    @Delete
    void delete(com.obtech.scienta.model.Movie.Movie movie);

    @Query("SELECT * FROM movie WHERE id = :id")
    LiveData<com.obtech.scienta.model.Movie.Movie> getMovieById(int id);

}
