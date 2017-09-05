package com.example.android.tinytrailersutility;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.tinytrailersutility.bus.BusProvider;
import com.example.android.tinytrailersutility.database.TinyDbContract;
import com.example.android.tinytrailersutility.services.DatabaseService;
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
    private static final String CASH_TOTAL_KEY = "cash-total-key";
    private static final int PREFERENCES_ERROR = -1;
    private static final int UNIQUE_ID_FOR_LOADER = 1986;
    private String mMovieId;
    private Bus mBus = BusProvider.getInstance();
    private String mTicketsSold;

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
    @BindView(R.id.frag_m1_movie_container)
    ConstraintLayout mScreenOneContainer;
    @BindView(R.id.m1_empty_view) TextView mEmptyStateTextViewM1;
    @BindView(R.id.btn_complete_one)
    Button mCompleteOneButton;

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
        mCompleteOneButton.setVisibility(View.VISIBLE);
        mCompleteOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeMovie();
                showEmptyState();
            }
        });
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

            if (data.getString(data.getColumnIndexOrThrow(TinyDbContract.TinyDbEntry.COLUMN_MOVIE_YOUTUBE_ID))
                    .equals(sharedPrefs.getString(SCREEN_ONE_KEY, "defaultValue"))) {

                // Movie ID
                mMovieId = data.getString(data.getColumnIndexOrThrow(
                        TinyDbContract.TinyDbEntry.COLUMN_MOVIE_YOUTUBE_ID));

                // Movie name
                mScreenOneMovieNameTextView.setText(
                        data.getString(data.getColumnIndexOrThrow(
                                TinyDbContract.TinyDbEntry.COLUMN_MOVIE_NAME)));

                // Tickets
                mTicketsSold = data.getString(data.getColumnIndexOrThrow(
                        TinyDbContract.TinyDbEntry.COLUMN_TICKETS_SOLD));
                mScreenOneTicketsSold.setText(mTicketsSold);

                // Timer
                String startTime = data.getString(data.getColumnIndexOrThrow(
                        TinyDbContract.TinyDbEntry.COLUMN_START_TIME));
                String rentalLength = data.getString(data.getColumnIndexOrThrow(
                        TinyDbContract.TinyDbEntry.COLUMN_RENTAL_LENGTH));
                long timeLeft = MyTimeUtils.getTimeLeftInMillis(startTime, rentalLength);
                startTimer(timeLeft);

                hideEmptyState();

            } else {
                showEmptyState();
            }
        }

        if (!data.moveToFirst()) {
            showEmptyState();
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
                mCompleteOneButton.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void completeMovie() {
        int ticketsSoldTotal = Integer.valueOf(mTicketsSold);

        SharedPreferences sharedPrefs = getContext()
                .getSharedPreferences("tinytrailerutilityScreenSettings", Context.MODE_PRIVATE);
        int currentTotal = sharedPrefs.getInt(CASH_TOTAL_KEY, PREFERENCES_ERROR);
        if (currentTotal == PREFERENCES_ERROR) return;

        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(CASH_TOTAL_KEY, ticketsSoldTotal);
        editor.commit();

        // Delete movie
        DatabaseService databaseService = new DatabaseService(getContext(), null);
        databaseService.deleteMovieById(mMovieId);
    }

    private void showEmptyState() {
        mScreenOneContainer.setVisibility(View.INVISIBLE);
        mEmptyStateTextViewM1.setVisibility(View.VISIBLE);
        mCompleteOneButton.setVisibility(View.INVISIBLE);
    }

    private void hideEmptyState() {
        mScreenOneContainer.setVisibility(View.VISIBLE);
        mEmptyStateTextViewM1.setVisibility(View.INVISIBLE);
    }
}
