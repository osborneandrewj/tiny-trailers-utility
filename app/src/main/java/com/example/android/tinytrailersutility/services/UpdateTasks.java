package com.example.android.tinytrailersutility.services;

import android.content.Context;
import android.util.Log;

import com.example.android.tinytrailersutility.rest.MovieService;
import com.example.android.tinytrailersutility.rest.YouTubeApi;
import com.example.android.tinytrailersutility.rest.YouTubeApiClient;

import java.util.ArrayList;

/**
 * Created by Zark on 8/21/2017.
 *
 */

public class UpdateTasks {

    public static final String TAG = UpdateTasks.class.getSimpleName();
    public static final String ACTION_UPDATE_VIEW_COUNT = "update-view-count";
    private static YouTubeApi mService;
    private DatabaseService mDatabaseService;

    public static void executeTask(Context context, String action) {
        switch (action) {
            case ACTION_UPDATE_VIEW_COUNT:
                updateViewCount(context);
                break;
            default:
                updateViewCount(context);
        }
    }

    private static void updateViewCount(Context context) {
        // Update the movie list
        DatabaseService databaseService = new DatabaseService(context, null);

        //MovieService movieService = new MovieService(buildApi(), )
        ArrayList<String> list = databaseService.getYouTubeIdListFromDatabase(context);
        for (String id : list) {

        }
        Log.v(TAG, "updaing View Count!");
    }

    private static YouTubeApi buildApi() {
        if (mService == null) {
            mService = YouTubeApiClient.getClient().create(YouTubeApi.class);
            return mService;
        } else return mService;
    }
}
