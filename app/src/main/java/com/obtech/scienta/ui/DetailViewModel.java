package com.obtech.scienta.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.obtech.scienta.model.Movie.Movie;
import com.obtech.scienta.model.Review.Review;
import com.obtech.scienta.model.Trailer.Trailer;
import com.obtech.scienta.database.MovieRepository;

import java.util.List;

public class DetailViewModel extends AndroidViewModel {

    private MovieRepository mRepository;
    private LiveData<List<com.obtech.scienta.model.Review.Review>> mReviews;
    private LiveData<List<com.obtech.scienta.model.Trailer.Trailer>> mTrailers;

    public DetailViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MovieRepository(application);
    }

    public LiveData<List<Review>> getReviews(int id, String apiKey) {
        return mRepository.getReviews(id, apiKey);
    }

    public LiveData<List<Trailer>> getTrailers(int id, String apiKey) {
        return mRepository.getTrailers(id, apiKey);
    }

    public void saveMovie(com.obtech.scienta.model.Movie.Movie movie) {
        mRepository.insert(movie);
    }

    public void deleteMovie(com.obtech.scienta.model.Movie.Movie movie) {
        mRepository.delete(movie);
    }

    public LiveData<Movie> loadFavById(int id) {
        return mRepository.getMovieById(id);
    }
}
