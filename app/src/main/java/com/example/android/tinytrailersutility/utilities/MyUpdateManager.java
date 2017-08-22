package com.example.android.tinytrailersutility.utilities;

import android.content.Context;

import com.example.android.tinytrailersutility.rest.YouTubeApiClient;
import com.squareup.otto.Bus;

/**
 * Created by osbor on 8/22/2017.
 *
 */

public class MyUpdateManager {

    private Context mContext;
    private Bus mBus;
    private YouTubeApiClient sYouTubeApiClient;

    public MyUpdateManager(Context context, Bus bus) {
        mContext = context;
        mBus = bus;
    }
}
