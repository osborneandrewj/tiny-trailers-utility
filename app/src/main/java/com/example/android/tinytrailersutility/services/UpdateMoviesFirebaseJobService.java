package com.example.android.tinytrailersutility.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.tinytrailersutility.models.youtube.YoutubeMovie;
import com.example.android.tinytrailersutility.rest.MovieService;
import com.example.android.tinytrailersutility.rest.YouTubeApi;
import com.example.android.tinytrailersutility.rest.YouTubeApiClient;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Zark on 8/21/2017.
 *
 */

public final class UpdateMoviesFirebaseJobService extends JobService {

    private AsyncTask mBackgroundTask;
    private ArrayList<String> mIdList;
    private YouTubeApi mYouTubeApi;

    @Override
    public boolean onStartJob(final JobParameters job) {

        Log.v("TAG", "Starting a job...");

        mBackgroundTask = new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = UpdateMoviesFirebaseJobService.this;
                final DatabaseService databaseService = new DatabaseService(context, null);
                mIdList = databaseService.getYouTubeIdListFromDatabase(null);
                String idString = android.text.TextUtils.join(",", mIdList);
                Log.v("TAG", "String: " + idString);


                mYouTubeApi = YouTubeApiClient.getClient().create(YouTubeApi.class);

                if (mIdList == null) return null;

                final Call<YoutubeMovie> updateMoviesSilently = mYouTubeApi
                        .getMultipleMovieDetails(
                                idString,
                                MovieService.mKey,
                                MovieService.mStatistics);
                updateMoviesSilently.enqueue(new Callback<YoutubeMovie>() {
                    @Override
                    public void onResponse(Call<YoutubeMovie> call, Response<YoutubeMovie> response) {
                        YoutubeMovie youtubeMovie = response.body();
                        Log.v("TAG", "Response: " + call.request().url());
                        databaseService.updateTicketsSold(response.body());
                    }

                    @Override
                    public void onFailure(Call<YoutubeMovie> call, Throwable t) {

                    }
                });


                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                //Context context = UpdateMoviesFirebaseJobService.this;
                //MovieService movieService = new MovieService(null, null);
                //movieService.updateMoviesSilently(context, mIdList);
                jobFinished(job, false);
            }
        };

        mBackgroundTask.execute();

        return true; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        Log.d("TAG", "I think the job is done now");
        return true; // Should this job be retired?
    }
}
