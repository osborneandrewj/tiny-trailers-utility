package com.example.android.tinytrailersutility.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.tinytrailersutility.database.TinyDbContract;
import com.example.android.tinytrailersutility.database.TinyDbHelper;
import com.example.android.tinytrailersutility.models.youtube.Item;
import com.example.android.tinytrailersutility.models.youtube.Snippet;
import com.example.android.tinytrailersutility.models.youtube.Statistics;
import com.example.android.tinytrailersutility.models.youtube.YoutubeMovie;
import com.example.android.tinytrailersutility.rest.YouTubeApi;
import com.squareup.otto.Bus;

import java.util.ArrayList;

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

    public void addTinyMovieToDatabase(YoutubeMovie aYoutubeMovie, @Nullable String rentalLength) {

        Item item = aYoutubeMovie.getItems().get(0);
        Statistics statistics = item.getStatistics();
        Snippet snippet = item.getSnippet();


        ContentValues values = new ContentValues();
        values.put(TinyDbContract.TinyDbEntry.COLUMN_MOVIE_URI, "0");
        values.put(TinyDbContract.TinyDbEntry.COLUMN_MOVIE_YOUTUBE_ID, item.getId());
        values.put(TinyDbContract.TinyDbEntry.COLUMN_MOVIE_NAME, snippet.getTitle());
        if (rentalLength == null) {
            values.put(TinyDbContract.TinyDbEntry.COLUMN_RENTAL_LENGTH, 0);
        } else {
            values.put(TinyDbContract.TinyDbEntry.COLUMN_RENTAL_LENGTH, rentalLength);
        }
        values.put(TinyDbContract.TinyDbEntry.COLUMN_START_TIME, String.valueOf(System.currentTimeMillis()));
        values.put(TinyDbContract.TinyDbEntry.COLUMN_STARTING_VIEWS, statistics.getViewCount());
        values.put(TinyDbContract.TinyDbEntry.COLUMN_CURRENT_VIEWS, statistics.getViewCount());

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

    public void updateTinyMovieViews(YoutubeMovie updatedMovieStats) {
        // Extract view count
        Item YouTubeItem = updatedMovieStats.getItems().get(0);
        Statistics YouTubeStats = YouTubeItem.getStatistics();
        String currentViews = YouTubeStats.getViewCount();
        String[] selectionArgs = new String[] {YouTubeItem.getId()};

        ContentValues contentValues = new ContentValues();
        contentValues.put(TinyDbContract.TinyDbEntry.COLUMN_CURRENT_VIEWS, currentViews);
        int updateInt = mContext.getContentResolver().update(
                TinyDbContract.TinyDbEntry.CONTENT_URI,
                contentValues,
                null,
                selectionArgs
        );

        Log.v(TAG, "updating view count to: " + currentViews);

        // Post to bus
    }

    public ArrayList<String> getYouTubeIdListFromDatabase() {
        TinyDbHelper helper = new TinyDbHelper(mContext);
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = database.query(
                TinyDbContract.TinyDbEntry.TABLE_NAME,
                mFullProjection,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<String> youTubeIdList = new ArrayList<>();
        //cursor.moveToFirst();
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndexOrThrow(
                    TinyDbContract.TinyDbEntry.COLUMN_MOVIE_YOUTUBE_ID
            )) != null) {
                youTubeIdList.add(cursor.getString(cursor.getColumnIndexOrThrow(
                        TinyDbContract.TinyDbEntry.COLUMN_MOVIE_YOUTUBE_ID)));
            }
        }

        if (cursor != null) cursor.close();

        return youTubeIdList;
    }
}
