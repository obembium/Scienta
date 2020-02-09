package com.obtech.scienta.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.obtech.scienta.model.Movie.Movie;
import com.obtech.scienta.database.MovieRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private MovieRepository mRepository;
    private LiveData<List<com.obtech.scienta.model.Movie.Movie>> mAllMovies;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MovieRepository(application);
    }

    LiveData<List<com.obtech.scienta.model.Movie.Movie>> loadAllMovies(String sort, String apiKey, int page) {
        return mAllMovies = mRepository.getMoviesFromNetwork(sort, apiKey,page);
    }
    LiveData<List<com.obtech.scienta.model.Movie.Movie>> loadSearchMovies(String query, String apiKey, int page) {
        return mAllMovies = mRepository.getSearchedMoviesFromNetwork(query, apiKey,page);
    }


    LiveData<List<com.obtech.scienta.model.Movie.Movie>> getFavMovies() {
        return mRepository.getFavMovies();
    }

    public LiveData<Movie> loadFavById(int id) {
        return mRepository.getMovieById(id);
    }
}
