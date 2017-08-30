package com.example.android.tinytrailersutility.services;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.tinytrailersutility.BuildConfig;
import com.example.android.tinytrailersutility.bus.OnMovieReceivedEvent;
import com.example.android.tinytrailersutility.bus.OnMovieStatsReceivedEvent;
import com.example.android.tinytrailersutility.models.youtube.YoutubeMovie;
import com.example.android.tinytrailersutility.rest.YouTubeApi;
import com.example.android.tinytrailersutility.services.DatabaseService;
import com.squareup.otto.Bus;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zark on 8/25/17.
 *
 */

public class MovieService {

    public static final String mKey = BuildConfig.YOUTUBE_API_KEY;
    public static final String mStatistics = "statistics";
    public static final String mStatisticsAndSnippet = "statistics,snippet";

    private YouTubeApi mYouTubeApi;
    private Bus mBus;
    private String mYoutubeId;

    public MovieService(@Nullable YouTubeApi aApi, @Nullable Bus aBus) {
        mYouTubeApi = aApi;
        mBus = aBus;
    }


    public void getMovieStatistics(String aMovieId) {
        final Call<YoutubeMovie> getStatistics = mYouTubeApi
                .getMovieDetails(aMovieId, mKey, mStatistics);
        getStatistics.enqueue(new Callback<YoutubeMovie>() {
            @Override
            public void onResponse(Call<YoutubeMovie> call, Response<YoutubeMovie> response) {
                Log.v("TAG", "Movie stats received");
                mBus.post(new OnMovieStatsReceivedEvent(response.body()));
            }

            @Override
            public void onFailure(Call<YoutubeMovie> call, Throwable t) {
                // post here
            }
        });
    }

    public void getMovieStatisticsAndSnippet(String aMovieid) {
        final Call<YoutubeMovie> getStatisticsAndSnippet = mYouTubeApi
                .getMovieDetails(aMovieid, mKey, mStatisticsAndSnippet);
        getStatisticsAndSnippet.enqueue(new Callback<YoutubeMovie>() {
            @Override
            public void onResponse(Call<YoutubeMovie> call, Response<YoutubeMovie> response) {
                YoutubeMovie youtubeMovie = response.body();
                Log.v("TAG", "Response: " + call.request().url());
                mBus.post(new OnMovieReceivedEvent(youtubeMovie));
            }

            @Override
            public void onFailure(Call<YoutubeMovie> call, Throwable error) {
                mBus.post(error);
            }
        });
    }

    public void updateAllMovies(Context context, String idList) {

        if (idList == null) return;

        final Call<YoutubeMovie> updateMoviesSilently = mYouTubeApi
                .getMultipleMovieDetails(idList, mKey, mStatistics);
        updateMoviesSilently.enqueue(new Callback<YoutubeMovie>() {
            @Override
            public void onResponse(Call<YoutubeMovie> call, Response<YoutubeMovie> response) {
                YoutubeMovie youtubeMovie = response.body();
                Log.v("TAG", "Response: " + call.request().url());
            }

            @Override
            public void onFailure(Call<YoutubeMovie> call, Throwable t) {

            }
        });
    }
}
