package com.example.android.tinytrailersutility.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Zark on 8/21/2017.
 *
 */

public class UpdateMoviesFirebaseJobService extends JobService {

    private AsyncTask mBackgroundTask;

    @Override
    public boolean onStartJob(final JobParameters job) {

        Log.v("TAG", "Starting a job...");

        // asynctask was here

        return false; // Answers the question: "Is there still work going on?"
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        Log.d("TAG", "I think the job is done now");
        return false; // Should this job be retired?
    }
}
