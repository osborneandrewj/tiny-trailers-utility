package com.example.android.tinytrailersutility;

import android.net.Uri;
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
import com.example.android.tinytrailersutility.models.youtube.YoutubeMovie;
import com.example.android.tinytrailersutility.rest.MovieService;
import com.example.android.tinytrailersutility.rest.YouTubeApi;
import com.example.android.tinytrailersutility.rest.YouTubeApiClient;
import com.example.android.tinytrailersutility.services.DatabaseService;
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
    private YouTubeApi mService;
    private String mYoutubeId;
    private Uri mYoutubeUri;

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

        mBtnSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildUrlFromUserEntry();
                selectVideo();
            }
        });

        if (mMovieService == null) {
            mMovieService = new MovieService(buildApi(), mBus);
        }

        if (mDatabaseService == null) {
            mDatabaseService = new DatabaseService(this, mBus);
        }

        mBus.register(mMovieService); // is this needed?
        mBus.register(mDatabaseService); // is this needed?
        mBus.register(this);
    }

    private void buildUrlFromUserEntry() {
        if (!TextUtils.isEmpty(mLinkEditText.getText().toString())) {
            String inputString = mLinkEditText.getText().toString();

            mYoutubeUri = MyLinkUtils.buildUriFromString(inputString);
            mYoutubeId = MyLinkUtils.getYoutubeIdFromLink(inputString);
        }
        // Reset the EditText
        mLinkEditText.setText("");
    }

    private void selectVideo() {

        if (mYoutubeId == null) return;
        mMovieService.getMovieStatisticsAndSnippet(mYoutubeId);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        adapterView.getItemAtPosition(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onStop() {
        super.onStop();
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

    @Subscribe
    public void newMovie(YoutubeMovie newMovie) {
        Log.v(TAG, "Got a movie! " + newMovie.getKind());
        // Do I need to separate the YouTube request from the database update?
        mDatabaseService.addTinyMovieToDatabase(newMovie);
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
