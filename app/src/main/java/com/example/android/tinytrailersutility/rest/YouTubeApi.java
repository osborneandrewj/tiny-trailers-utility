package com.example.android.tinytrailersutility.rest;

import com.example.android.tinytrailersutility.models.Movie;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Zark on 8/15/2017.
 *
 */

public interface YouTubeApi {

    // Get a list of movie statistics
    @GET("videos")
    Call<Movie> getMovieStatistics(
            @Query("id") String movieId,
            @Query("key") String apiKey,
            @Query("part") String part);
}
