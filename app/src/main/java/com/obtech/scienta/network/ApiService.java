package com.obtech.scienta.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.obtech.scienta.model.Movie.Movie;
import com.obtech.scienta.model.Movie.Result;
import com.obtech.scienta.model.Review.Review;
import com.obtech.scienta.model.Review.ReviewResponse;
import com.obtech.scienta.model.Trailer.Trailer;
import com.obtech.scienta.model.Trailer.TrailerResponse;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

    private static ApiService apiService = null;
    private static ApiInterface apiInterface;

    private ApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstants.MOVIES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiInterface = retrofit.create(ApiInterface.class);

    }

    public synchronized static ApiService getInstance() {
        if (apiService == null) {
            apiService = new ApiService();
        }
        return apiService;
    }

    public LiveData<List<com.obtech.scienta.model.Movie.Movie>> getMovies(String sort, String apiKey, int page) {
        final MutableLiveData<List<com.obtech.scienta.model.Movie.Movie>> mutableLiveData = new MutableLiveData<>();
        apiInterface.getMovies(sort, apiKey, page+"").enqueue(new Callback<com.obtech.scienta.model.Movie.Result>() {
            @Override
            public void onResponse(Call<com.obtech.scienta.model.Movie.Result> call, Response<com.obtech.scienta.model.Movie.Result> response) {
                int statusCode = response.code();
                Log.d(ApiService.class.getSimpleName(), "onResponse: " + statusCode);
                if(statusCode==200)
                mutableLiveData.setValue(response.body().getResults());
            }

            @Override
            public void onFailure(Call<com.obtech.scienta.model.Movie.Result> call, Throwable t) {
                mutableLiveData.setValue(null);
                Log.e(ApiService.class.getSimpleName(), "onResponse: " + t.getMessage());
                t.printStackTrace();
            }
        });
        return mutableLiveData;
    }


    public LiveData<List<com.obtech.scienta.model.Movie.Movie>> getSearchMovies(String query, String apiKey, int page) {
        final MutableLiveData<List<Movie>> mutableLiveData = new MutableLiveData<>();
        apiInterface.getSearchMovies(query, apiKey, page+"").enqueue(new Callback<com.obtech.scienta.model.Movie.Result>() {
            @Override
            public void onResponse(Call<com.obtech.scienta.model.Movie.Result> call, Response<com.obtech.scienta.model.Movie.Result> response) {
                int statusCode = response.code();
                Log.d(ApiService.class.getSimpleName(), "onResponse: " + statusCode);
                if(statusCode==200)
                    mutableLiveData.setValue(response.body().getResults());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                mutableLiveData.setValue(null);
                Log.e(ApiService.class.getSimpleName(), "onResponse: " + t.getMessage());
                t.printStackTrace();
            }
        });
        return mutableLiveData;
    }

    public LiveData<List<com.obtech.scienta.model.Review.Review>> getReviews(Integer id, String apiKey) {
        final MutableLiveData<List<Review>> mutableLiveData = new MutableLiveData<>();
        apiInterface.getReviews(id, apiKey).enqueue(new Callback<com.obtech.scienta.model.Review.ReviewResponse>() {
            @Override
            public void onResponse(Call<com.obtech.scienta.model.Review.ReviewResponse> call, Response<com.obtech.scienta.model.Review.ReviewResponse> response) {
                int statusCode = response.code();
                mutableLiveData.setValue(response.body().getResult());
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }

    public LiveData<List<com.obtech.scienta.model.Trailer.Trailer>> getTrailers(Integer id, String apiKey) {
        final MutableLiveData<List<Trailer>> mutableLiveData = new MutableLiveData<>();
        apiInterface.getTrailers(id, apiKey).enqueue(new Callback<com.obtech.scienta.model.Trailer.TrailerResponse>() {
            @Override
            public void onResponse(Call<com.obtech.scienta.model.Trailer.TrailerResponse> call, Response<com.obtech.scienta.model.Trailer.TrailerResponse> response) {
                int statusCode = response.code();
                mutableLiveData.setValue(response.body().getResults());
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {

                mutableLiveData.setValue(null);
            }
        });
        return mutableLiveData;
    }

}
