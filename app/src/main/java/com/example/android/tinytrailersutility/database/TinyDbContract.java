package com.example.android.tinytrailersutility.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by zark on 8/20/17.
 *
 */

public class TinyDbContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.tinytrailersutility";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE_RENTALS = "tinyMovies";

    private TinyDbContract() {
        // This class should never be initialized
    }

    public static class TinyDbEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,
                PATH_MOVIE_RENTALS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_MOVIE_RENTALS;

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_MOVIE_RENTALS;

        /** Name of database table for items */
        public static final String TABLE_NAME = "rentedMovies";

        /** Names of each column in the table. Note: _ID is assumed already by BaseColumns */
        public static final String COLUMN_MOVIE_URI = "movie_uri";
        public static final String COLUMN_MOVIE_YOUTUBE_ID = "youtube_id";
        public static final String COLUMN_MOVIE_NAME = "movie_name";
        public static final String COLUMN_RENTAL_LENGTH = "rental_length";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_STARTING_VIEWS = "starting_views";
        public static final String COLUMN_CURRENT_VIEWS = "current_views";
        public static final String COLUMN_TICKETS_SOLD = "tickets_sold";


        /** Used to create the table */
        public static final String SQL_CREATE_RENTAL_TABLE = "CREATE TABLE " +
                TinyDbEntry.TABLE_NAME + " (" +
                TinyDbEntry._ID + " INTEGER PRIMARY KEY," +
                TinyDbEntry.COLUMN_MOVIE_URI + " TEXT NOT NULL," +
                TinyDbEntry.COLUMN_MOVIE_YOUTUBE_ID + " TEXT NOT NULL," +
                TinyDbEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL," +
                TinyDbEntry.COLUMN_RENTAL_LENGTH + " TEXT NOT NULL," +
                TinyDbEntry.COLUMN_START_TIME + " TEXT NOT NULL," +
                TinyDbEntry.COLUMN_STARTING_VIEWS + " TEXT NOT NULL," +
                TinyDbEntry.COLUMN_CURRENT_VIEWS + " TEXT NOT NULL," +
                TinyDbEntry.COLUMN_TICKETS_SOLD + " TEXT NOT NULL)";

        /** Used to delete the table */
        public static final String SQL_DELETE_RENTAL_TABLE =
                "DROP TABLE IF EXISTS " + TinyDbEntry.TABLE_NAME;

    }

}
