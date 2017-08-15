package com.example.android.tinytrailersutility.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

/**
 * Created by zark on 8/14/17.
 *
 */

public class MyNetworkUtils {

    public static Uri buildUriFromString(String aString) {
        Uri uri = null;
        try {
            uri = Uri.parse(aString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    /**
     * Check for internet connection.
     *
     * @param context The context.
     * @return The boolean "true" if internet connection exists.
     */
    public static boolean doesNetworkConnectionExist(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
