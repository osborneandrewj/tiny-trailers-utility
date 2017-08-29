package com.example.android.tinytrailersutility.services;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.tinytrailersutility.database.TinyDbContract;
import com.example.android.tinytrailersutility.models.youtube.Item;
import com.example.android.tinytrailersutility.models.youtube.Snippet;
import com.example.android.tinytrailersutility.models.youtube.Statistics;
import com.example.android.tinytrailersutility.models.youtube.YoutubeMovie;
import com.example.android.tinytrailersutility.rest.YouTubeApi;
import com.squareup.otto.Bus;

/**
 * Created by zark on 8/28/17.
 *
 */

public class DatabaseService {

    private static final String TAG = DatabaseService.class.getSimpleName();
    private Bus mBus;
    private Context mContext;

    private static final int UPDATE_SUCCESSFUL = 100;
    private static final int UPDATE_FAILED = -1;
    private static YouTubeApi mService;
    private static Item mYoutubeItem;
    private static Uri mYoutubeUri;
    private static Statistics mMovieStats;
    private static YoutubeMovie mYoutubeMovie;
    private static String mYoutubeId;

    private static String [] mFullProjection = {
            TinyDbContract.TinyDbEntry._ID,
            TinyDbContract.TinyDbEntry.COLUMN_MOVIE_YOUTUBE_ID,
            TinyDbContract.TinyDbEntry.COLUMN_MOVIE_URI,
            TinyDbContract.TinyDbEntry.COLUMN_MOVIE_NAME,
            TinyDbContract.TinyDbEntry.COLUMN_RENTAL_LENGTH,
            TinyDbContract.TinyDbEntry.COLUMN_START_TIME,
            TinyDbContract.TinyDbEntry.COLUMN_STARTING_VIEWS,
            TinyDbContract.TinyDbEntry.COLUMN_CURRENT_VIEWS};

    public DatabaseService(Context context, Bus bus) {
        mContext = context;
        mBus = bus;
    }

    public void addTinyMovieToDatabase(YoutubeMovie aYoutubeMovie) {

        mYoutubeItem = mYoutubeMovie.getItems().get(0);
        mMovieStats = mYoutubeItem.getStatistics();
        Snippet movieSnippet = mYoutubeItem.getSnippet();
        String movieTitle = movieSnippet.getTitle();

        ContentValues values = new ContentValues();
        values.put(TinyDbContract.TinyDbEntry.COLUMN_MOVIE_URI, mYoutubeUri.toString());
        values.put(TinyDbContract.TinyDbEntry.COLUMN_MOVIE_YOUTUBE_ID, mYoutubeId);
        values.put(TinyDbContract.TinyDbEntry.COLUMN_MOVIE_NAME, movieTitle);
        values.put(TinyDbContract.TinyDbEntry.COLUMN_RENTAL_LENGTH, 0);
        values.put(TinyDbContract.TinyDbEntry.COLUMN_START_TIME, String.valueOf(System.currentTimeMillis()));
        values.put(TinyDbContract.TinyDbEntry.COLUMN_STARTING_VIEWS, mMovieStats.getViewCount());
        values.put(TinyDbContract.TinyDbEntry.COLUMN_CURRENT_VIEWS, mMovieStats.getViewCount());

        Uri newUri = mContext.getContentResolver().insert(
                TinyDbContract.TinyDbEntry.CONTENT_URI,
                values);
        Log.v(TAG, "New movie rented! " + newUri);

        // Post to bus
        if (newUri == null) {
            mBus.post(UPDATE_FAILED);
        } else {
            mBus.post(UPDATE_SUCCESSFUL);
        }
    }

    public void updateTinyMovieViews(YoutubeMovie updateMovie, Uri movieUriToBeUpdated) {
        // Extract view count
        Item YouTubeItem = updateMovie.getItems().get(0);
        Statistics YouTubeStats = YouTubeItem.getStatistics();
        String currentViews = YouTubeStats.getViewCount();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TinyDbContract.TinyDbEntry.COLUMN_CURRENT_VIEWS, currentViews);
        int updateInt = mContext.getContentResolver().update(
                movieUriToBeUpdated,
                contentValues,
                null,
                null
        );

        Log.v(TAG, "updating view count to: " + currentViews);

        // Post to bus
    }
}
