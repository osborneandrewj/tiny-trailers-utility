package com.example.android.tinytrailersutility.services;

import android.content.Context;

/**
 * Created by Zark on 8/21/2017.
 *
 */

public class UpdateTasks {

    public static final String ACTION_UPDATE_VIEW_COUNT = "update-view-count";

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

    }
}
