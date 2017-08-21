package com.example.android.tinytrailersutility.utilities;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.HttpUrl;

/**
 * Created by zark on 8/19/17.
 *
 */

public class MyLinkUtils {

    private static final String TAG = MyLinkUtils.class.getSimpleName();

    public static String getYoutubeIdFromLink(String aLink) {
        if (TextUtils.isEmpty(aLink)) {
            Log.v(TAG, "Something isn't right...");
            return null;
        }

        Uri uri = buildUriFromString(aLink);
        String id = "";
        if (uri.getQueryParameter("v") != null) {
            id = uri.getQueryParameter("v");
            Log.v(TAG, "Got a query parameter: " + id);
        } else {
            id = uri.getLastPathSegment();
            Log.v(TAG, "Got a path segment: " + id);
        }
        return id;
    }

    public static Uri buildUriFromString(@NonNull String aLink) {
        Uri uri = Uri.parse(aLink);
        return uri;
    }
}
