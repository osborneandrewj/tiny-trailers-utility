package com.example.android.tinytrailersutility.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Zark on 8/21/2017.
 *
 */

public class TinyProvider extends ContentProvider {

    private static final String TAG = TinyProvider.class.getSimpleName();
    private TinyDbHelper mTinyDbHelper;
    private static final int TINY_DB = 100;
    private static final int TINY_DB_ITEM = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(
                TinyDbContract.CONTENT_AUTHORITY,
                TinyDbContract.PATH_MOVIE_RENTALS,
                TINY_DB);
        sUriMatcher.addURI(
                TinyDbContract.CONTENT_AUTHORITY,
                TinyDbContract.PATH_MOVIE_RENTALS + "/#",
                TINY_DB_ITEM);
    }

    @Override
    public boolean onCreate() {
        mTinyDbHelper = new TinyDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase database = mTinyDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case TINY_DB:
                cursor = database.query(TinyDbContract.TinyDbEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TINY_DB_ITEM:
                selection = TinyDbContract.TinyDbEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TinyDbContract.TinyDbEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query this: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TINY_DB:
                return insertTinyMovie(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion not supported for: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TINY_DB:
                return deleteTinyMovie(uri, selection, selectionArgs);
            case TINY_DB_ITEM:
                selection = TinyDbContract.TinyDbEntry._ID;
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return deleteTinyMovie(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not allowed for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TINY_DB:
                return 0;
            case TINY_DB_ITEM:
                selection = TinyDbContract.TinyDbEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateTinyMovie(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is now allowed for " + uri);
        }
    }

    private Uri insertTinyMovie(Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mTinyDbHelper.getWritableDatabase();
        long id_value = database.insert(TinyDbContract.TinyDbEntry.TABLE_NAME, null, contentValues);

        if (id_value == -1) {
            Log.e(TAG, "Failed to insert new movie for: " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id_value);
    }

    private int deleteTinyMovie(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mTinyDbHelper.getWritableDatabase();

        int numberOfRowsDeleted = database.delete(
                TinyDbContract.TinyDbEntry.TABLE_NAME,
                selection + "=?",
                selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return numberOfRowsDeleted;
    }

    private int updateTinyMovie(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mTinyDbHelper.getWritableDatabase();

        int rowsAffected = database.update(TinyDbContract.TinyDbEntry.TABLE_NAME,
                contentValues,
                selection,
                selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return rowsAffected;
    }
}
