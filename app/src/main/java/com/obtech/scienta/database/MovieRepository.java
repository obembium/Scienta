package com.obtech.scienta.database;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.obtech.scienta.AppExecutors;
import com.obtech.scienta.model.Movie.Movie;
import com.obtech.scienta.model.Review.Review;
import com.obtech.scienta.model.Trailer.Trailer;
import com.obtech.scienta.network.ApiService;

import java.util.List;

public class MovieRepository {
    private ApiService apiService = ApiService.getInstance();
    private MovieDao mDao;
    private LiveData<List<com.obtech.scienta.model.Movie.Movie>> mFavMovies;
    private com.obtech.scienta.AppExecutors mExecutors = AppExecutors.getInstance();

    public MovieRepository(Application application) {
        MovieDb db = MovieDb.getDatabase(application);
        mDao = db.movieDao();
        mFavMovies = mDao.getAllMovies();
    }

    //  for favorites
    public LiveData<List<com.obtech.scienta.model.Movie.Movie>> getFavMovies() {
        return mFavMovies;
    }

    public void insert(final com.obtech.scienta.model.Movie.Movie movie) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDao.insertMovie(movie);
            }
        });
    }

    public void delete(final com.obtech.scienta.model.Movie.Movie movie) {
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDao.delete(movie);
            }
        });
    }

    public LiveData<com.obtech.scienta.model.Movie.Movie> getMovieById(int id) {
        return mDao.getMovieById(id);
    }

    //for network
    public LiveData<List<com.obtech.scienta.model.Movie.Movie>> getMoviesFromNetwork(String sortType, String apiKey, int page) {
        return apiService.getMovies(sortType, apiKey,page);
    }
    //for network
    public LiveData<List<Movie>> getSearchedMoviesFromNetwork(String query, String apiKey, int page) {
        return apiService.getSearchMovies(query, apiKey,page);
    }

    public LiveData<List<Review>> getReviews(Integer id, String apiKey) {
        return apiService.getReviews(id, apiKey);
    }

    public LiveData<List<Trailer>> getTrailers(Integer id, String apiKey) {
        return apiService.getTrailers(id, apiKey);
    }
}
