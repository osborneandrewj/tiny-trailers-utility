package com.example.android.tinytrailersutility.utilities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.tinytrailersutility.services.UpdateMoviesFirebaseJobService;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

/**
 * Created by Zark on 8/21/2017.
 *
 */

public class FirebaseJobUtils {

    private static final int UPDATE_INTERVAL_MINUTES = 1;
    private static final int UPDATE_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(UPDATE_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = 30;

    private static final String UPDATE_JOB_TAG = "update_movies_tag";
    private static final String YOUTUBE_ID = "youtube_id";
    private static boolean sInitialized;

    public static void scheduleMovieUpdate(@NonNull final Context context) {

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));

        Job updateMovieJob = dispatcher.newJobBuilder()
                .setService(UpdateMoviesFirebaseJobService.class)
                .setTag(UPDATE_JOB_TAG)
                .setRecurring(true)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(Trigger.executionWindow(
                        UPDATE_INTERVAL_SECONDS,
                        UPDATE_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(false)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();

        dispatcher.mustSchedule(updateMovieJob);

        Log.v("TAG", "Job scheduled!");
    }

    /**
     * Schedule a job to grab movie information from YouTube at the end of the rental period
     * specified by the user.
     *
     * @param context context.
     * @param aYouTubeId This is used as the tag for the job and used by the job to fetch the
     *                   data from YouTube.
     * @param rentalPeriodInMinutes This is defined by the user when renting the movie.
     */
    public static void scheduleSpecificMovieUpdate(
            final Context context,
            String aYouTubeId,
            int rentalPeriodInMinutes) {

        Log.v("TAG", "Starting to schedule a job..." + aYouTubeId + " " + rentalPeriodInMinutes);
        // Error checking
        if (TextUtils.isEmpty(aYouTubeId) || aYouTubeId == null) {return;}
        if (rentalPeriodInMinutes == 0) {return;}

        // Schedule job
        int rentalPeriodInSeconds = (int) TimeUnit.MINUTES.toSeconds(rentalPeriodInMinutes);
        Bundle extras = new Bundle();
        extras.putString(YOUTUBE_ID, aYouTubeId);

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job updateMovieJob = dispatcher.newJobBuilder()
                .setService(UpdateMoviesFirebaseJobService.class)
                .setTag(aYouTubeId)
                .setExtras(extras)
                .setRecurring(false)
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                .setTrigger(Trigger.executionWindow(
                        rentalPeriodInSeconds,
                        rentalPeriodInSeconds + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();

        dispatcher.mustSchedule(updateMovieJob);

        Log.v("TAG", "Job scheduled!");

    }

    public static void cancelAllJobs(final Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.cancelAll();
        Log.v("TAG", "All jobs canceled");
    }

    synchronized public static void scheduleMovieUpdates(@NonNull final Context context) {
        if (sInitialized) {
            Log.v("FirebaseJobUtils", "Already initialized...");
            return;
        }

        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job updateMovieJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(UpdateMoviesFirebaseJobService.class)
                // uniquely identifies the job
                .setTag(UPDATE_JOB_TAG)
                // one-off job?
                .setRecurring(true)
                // don't persist past a device reboot
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // start between x and x+y seconds from now
                .setTrigger(Trigger.executionWindow(
                        UPDATE_INTERVAL_SECONDS,
                        UPDATE_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                // overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        Constraint.ON_ANY_NETWORK
                )
                .build();

        Log.v("FirebaseJobUtils", "Getting this running...");

        Job simpleJob = dispatcher.newJobBuilder()
                .setService(UpdateMoviesFirebaseJobService.class)
                .setTag("Weirdness")
                .build();

        dispatcher.mustSchedule(simpleJob);
        //sInitialized = true;
    }
}
