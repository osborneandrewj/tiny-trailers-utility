package com.example.android.tinytrailersutility;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.tinytrailersutility.bus.BusProvider;
import com.example.android.tinytrailersutility.database.TinyDbContract;
import com.example.android.tinytrailersutility.utilities.MyTimeUtils;
import com.squareup.otto.Bus;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScreenOneFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ScreenOneFragment.class.getSimpleName();
    private static final String SCREEN_ONE_KEY = "screen-one-key";
    private static final int UNIQUE_ID_FOR_LOADER = 1986;
    private Bus mBus = BusProvider.getInstance();

    @BindView(R.id.tv_movie_name_m1)
    TextView mScreenOneMovieNameTextView;
    @BindView(R.id.tv_time_left_m1)
    TextView mScreenOneTimeLeft;
    @BindView(R.id.label_time_left_m1)
    TextView mLabelScreenOneTimeLeft;
    @BindView(R.id.tv_tickets_sold_m1)
    TextView mScreenOneTicketsSold;
    @BindView(R.id.label_tickets_sold_m1)
    TextView mLabelScreenOneTicketsSold;

    public ScreenOneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_screen_one, container, false);
        ButterKnife.bind(this, view);
        mBus.register(this);
        getActivity().getSupportLoaderManager().initLoader(UNIQUE_ID_FOR_LOADER, null, this);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                TinyDbContract.TinyDbEntry._ID,
                TinyDbContract.TinyDbEntry.COLUMN_MOVIE_YOUTUBE_ID,
                TinyDbContract.TinyDbEntry.COLUMN_MOVIE_URI,
                TinyDbContract.TinyDbEntry.COLUMN_MOVIE_NAME,
                TinyDbContract.TinyDbEntry.COLUMN_RENTAL_LENGTH,
                TinyDbContract.TinyDbEntry.COLUMN_START_TIME,
                TinyDbContract.TinyDbEntry.COLUMN_STARTING_VIEWS,
                TinyDbContract.TinyDbEntry.COLUMN_CURRENT_VIEWS,
                TinyDbContract.TinyDbEntry.COLUMN_TICKETS_SOLD,
                TinyDbContract.TinyDbEntry.COLUMN_RENTAL_COMPLETE};
        return new CursorLoader(getContext(),
                TinyDbContract.TinyDbEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        SharedPreferences sharedPrefs = getContext()
                .getSharedPreferences("tinytrailerutilityScreenSettings", Context.MODE_PRIVATE);

        while (data.moveToNext()) {

            // Set screen one data
            if (data.getString(data.getColumnIndexOrThrow(TinyDbContract.TinyDbEntry.COLUMN_MOVIE_YOUTUBE_ID))
                    .equals(sharedPrefs.getString(SCREEN_ONE_KEY, "defaultValue"))) {
                mScreenOneMovieNameTextView.setText(
                        data.getString(data.getColumnIndexOrThrow(
                                TinyDbContract.TinyDbEntry.COLUMN_MOVIE_NAME)));
                mScreenOneTicketsSold.setText(data.getString(data.getColumnIndexOrThrow(
                        TinyDbContract.TinyDbEntry.COLUMN_TICKETS_SOLD)));
                // Timer
                String startTime = data.getString(data.getColumnIndexOrThrow(
                        TinyDbContract.TinyDbEntry.COLUMN_START_TIME));
                String rentalLength = data.getString(data.getColumnIndexOrThrow(
                        TinyDbContract.TinyDbEntry.COLUMN_RENTAL_LENGTH));
                long timeLeft = MyTimeUtils.getTimeLeftInMillis(startTime, rentalLength);
                Log.v(TAG, "time left: " + timeLeft);
                startTimer(timeLeft);

            } else {
                //setScreenToEmpty(SCREEN_ONE_KEY);
            }
        }

        if (!data.moveToFirst()) {
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onStop() {
        super.onStop();
        mBus.unregister(this);
    }

    private void startTimer(long aDuration) {
        new CountDownTimer(aDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mScreenOneTimeLeft.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                mScreenOneTimeLeft.setText("0");
            }
        }.start();

    }

    private void onOutOfTime() {

    }
}
