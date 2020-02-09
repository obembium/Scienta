package com.obtech.scienta.network;

import com.obtech.scienta.model.Movie.Result;
import com.obtech.scienta.model.Review.ReviewResponse;
import com.obtech.scienta.model.Trailer.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("movie/{sorting}")
    Call<com.obtech.scienta.model.Movie.Result> getMovies(@Path("sorting") String sort,
                                                          @Query(ApiConstants.API_KEY_LABEL) String apiKey,
                                                          @Query("page") String page);


    @GET("search/movie")
    Call<Result> getSearchMovies(@Query("query") String searchQuery,
                                 @Query(ApiConstants.API_KEY_LABEL) String apiKey,
                                 @Query("page") String page);

    @GET(ApiConstants.REVIEWS)
    Call<ReviewResponse> getReviews(@Path("id") int id, @Query(ApiConstants.API_KEY_LABEL) String apiKey);

    @GET(ApiConstants.TRAILERS)
    Call<TrailerResponse> getTrailers(@Path("id") int id, @Query(ApiConstants.API_KEY_LABEL) String apiKey);
}
