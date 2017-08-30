package com.example.android.tinytrailersutility.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Zark on 8/21/2017.
 *
 */

public class UpdateMoviesIntentService extends IntentService {

    public UpdateMoviesIntentService() {
        super("UpdateMoviesIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        //TODO: execute
        UpdateTasks.executeTask(this, action); // Do I need this? Don't think so...


        DatabaseService databaseService = new DatabaseService(this, null);
    }
}