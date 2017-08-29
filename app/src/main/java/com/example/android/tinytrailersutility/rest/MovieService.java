package com.example.android.tinytrailersutility.rest;

import android.util.Log;

import com.example.android.tinytrailersutility.BuildConfig;
import com.example.android.tinytrailersutility.bus.OnMovieReceivedEvent;
import com.example.android.tinytrailersutility.bus.OnMovieStatsReceivedEvent;
import com.example.android.tinytrailersutility.models.youtube.YoutubeMovie;
import com.squareup.otto.Bus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by zark on 8/25/17.
 *
 */

public class MovieService {

    private static final String mKey = BuildConfig.YOUTUBE_API_KEY;
    private static final String mStatistics = "statistics";
    private static final String mStatisticsAndSnippet = "statistics,snippet";

    private YouTubeApi mYouTubeApi;
    private Bus mBus;
    private String mYoutubeId;

    public MovieService(YouTubeApi aApi, Bus aBus) {
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
}
