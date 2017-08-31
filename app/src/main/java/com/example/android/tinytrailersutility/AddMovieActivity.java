package com.example.android.tinytrailersutility;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.android.tinytrailersutility.bus.BusProvider;
import com.example.android.tinytrailersutility.bus.OnMovieReceivedEvent;
import com.example.android.tinytrailersutility.models.youtube.YoutubeMovie;
import com.example.android.tinytrailersutility.services.MovieService;
import com.example.android.tinytrailersutility.rest.YouTubeApi;
import com.example.android.tinytrailersutility.rest.YouTubeApiClient;
import com.example.android.tinytrailersutility.services.DatabaseService;
import com.example.android.tinytrailersutility.utilities.FirebaseJobUtils;
import com.example.android.tinytrailersutility.utilities.MyLinkUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddMovieActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    @BindView(R.id.youtubeLinkEditText) EditText mLinkEditText;
    @BindView(R.id.spinner_rental_period) Spinner mRentalSpinner;
    @BindView(R.id.btn_select_video) Button mBtnSelectVideo;

    private static final String TAG = AddMovieActivity.class.getSimpleName();
    private static final int UPDATE_SUCCESSFUL = 100;
    private static final int UPDATE_FAILED = -1;
    private String mRentalLength;
    private String mYouTubeId;
    private YouTubeApi mService;

    private Bus mBus = BusProvider.getInstance();
    private MovieService mMovieService;
    private DatabaseService mDatabaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rental_length_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRentalSpinner.setAdapter(adapter);
        mRentalSpinner.setPrompt(getString(R.string.spinner_prompt));
        mRentalSpinner.setOnItemSelectedListener(this);

        mBtnSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectVideoButtonClicked();
            }
        });

        if (mMovieService == null) {
            mMovieService = new MovieService(buildApi(), mBus);
        }

        if (mDatabaseService == null) {
            mDatabaseService = new DatabaseService(this, mBus);
        }

        mBus.register(this);
    }

    private void onSelectVideoButtonClicked() {

        if (!TextUtils.isEmpty(mLinkEditText.getText().toString())) {

            String inputString = mLinkEditText.getText().toString();
            mYouTubeId = MyLinkUtils.getYoutubeIdFromLink(inputString);

            if (mYouTubeId == null) return;
            mMovieService.getMovieStatisticsAndSnippet(mYouTubeId);
        }
        // Clear the EditText
        mLinkEditText.setText("");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (position) {
            case 0:
                mRentalLength = "10";
                break;
            case 1:
                mRentalLength = "15";
                break;
            case 2:
                mRentalLength = "25";
                break;
            default:
                mRentalLength = "10";

        }

        Log.v(TAG, "Spinner: " + position + " " + mRentalLength);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private YouTubeApi buildApi() {
        if (mService == null) {
            mService = YouTubeApiClient.getClient().create(YouTubeApi.class);
            return mService;
        } else return mService;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    private void scheduleRentalJobs() {
        FirebaseJobUtils.scheduleSpecificMovieUpdate(
                this,
                mYouTubeId,
                Integer.parseInt(mRentalLength));
    }

    @Subscribe
    public void onMovieReceived(OnMovieReceivedEvent event) {
        YoutubeMovie newMovie = event.mNewMovie;
        Log.v(TAG, "Got an actual thing here: " + newMovie.getEtag());
        mDatabaseService.addTinyMovieToDatabase(newMovie, mRentalLength);
        scheduleRentalJobs();
        finish();
    }

    @Subscribe
    public void updateStatus(int updateStatus) {
        if (updateStatus == UPDATE_FAILED) {
            Log.v(TAG, "Update failed");
        }
        if (updateStatus == UPDATE_SUCCESSFUL) {
            Log.v(TAG, "Update successful");
        }
    }
}
