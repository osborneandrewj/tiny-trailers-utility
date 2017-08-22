package com.example.android.tinytrailersutility.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.tinytrailersutility.BuildConfig;
import com.example.android.tinytrailersutility.database.TinyDbContract;
import com.example.android.tinytrailersutility.models.youtube.Item;
import com.example.android.tinytrailersutility.models.youtube.Statistics;
import com.example.android.tinytrailersutility.models.youtube.YoutubeMovie;
import com.example.android.tinytrailersutility.rest.IYouTube;
import com.example.android.tinytrailersutility.rest.YouTubeApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Zark on 8/22/2017.
 *
 */

public class MyUpdateUtils {

    private static final String TAG = MyUpdateUtils.class.getSimpleName();
    private static IYouTube mService;
    private static Item mYoutubeItem;
    private static Statistics mMovieStats;
    private static YoutubeMovie mYoutubeMovie;
    private static String mCurrentViews;
    private static Uri mCurrentUri;
    private static Context mContext;
    private static String [] mFullProjection = {
            TinyDbContract.TinyDbEntry._ID,
            TinyDbContract.TinyDbEntry.COLUMN_MOVIE_YOUTUBE_ID,
            TinyDbContract.TinyDbEntry.COLUMN_MOVIE_URI,
            TinyDbContract.TinyDbEntry.COLUMN_MOVIE_NAME,
            TinyDbContract.TinyDbEntry.COLUMN_RENTAL_LENGTH,
            TinyDbContract.TinyDbEntry.COLUMN_START_TIME,
            TinyDbContract.TinyDbEntry.COLUMN_STARTING_VIEWS,
            TinyDbContract.TinyDbEntry.COLUMN_CURRENT_VIEWS};

    public static void updateMovie(Context context, Uri uri) {
        mContext = context;
        mCurrentUri = uri;
        Cursor cursor = context.getContentResolver().query(uri,
                mFullProjection,
                null,
                null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            String youtubeId = cursor.getString(cursor.getColumnIndexOrThrow(
                    TinyDbContract.TinyDbEntry.COLUMN_MOVIE_YOUTUBE_ID));
            getDataFromYoutube(youtubeId);
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    private static void getDataFromYoutube(String youtubeId) {
        if (youtubeId == null) return;
        if (mService == null) {
            mService = YouTubeApiClient.getClient().create(IYouTube.class);
        }
        final Call<YoutubeMovie> callMovie = mService.getMovieStatistics(
                youtubeId,
                BuildConfig.YOUTUBE_API_KEY,
                "statistics");
        callMovie.enqueue(new Callback<YoutubeMovie>() {
            @Override
            public void onResponse(Call<YoutubeMovie> call, Response<YoutubeMovie> response) {
                mYoutubeMovie = response.body();
                mYoutubeItem = mYoutubeMovie.getItems().get(0);
                Statistics movieStats = mYoutubeItem.getStatistics();
                String currentViews = movieStats.getViewCount();
                updateLocalViewCount(currentViews);
                Log.v(TAG, "Updating! " + callMovie.request().url());
            }

            @Override
            public void onFailure(Call<YoutubeMovie> call, Throwable t) {
                Log.v(TAG, "Hmm, something went wrong " + callMovie.request().url());
                t.printStackTrace();
            }
        });

    }

    private static void updateLocalViewCount(String currentViews) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TinyDbContract.TinyDbEntry.COLUMN_CURRENT_VIEWS, currentViews);
        int updateInt = mContext.getContentResolver().update(
                mCurrentUri,
                contentValues,
                null,
                null
        );

        Log.v(TAG, "updating? " + updateInt);
    }
}
