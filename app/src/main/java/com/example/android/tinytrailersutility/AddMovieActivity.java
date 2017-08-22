package com.example.android.tinytrailersutility;

import android.content.ContentValues;
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

import com.example.android.tinytrailersutility.database.TinyDbContract;
import com.example.android.tinytrailersutility.models.youtube.Item;
import com.example.android.tinytrailersutility.models.youtube.Snippet;
import com.example.android.tinytrailersutility.models.youtube.YoutubeMovie;
import com.example.android.tinytrailersutility.models.youtube.Statistics;
import com.example.android.tinytrailersutility.rest.YouTubeApi;
import com.example.android.tinytrailersutility.rest.YouTubeApiClient;
import com.example.android.tinytrailersutility.utilities.MyLinkUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMovieActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    @BindView(R.id.youtubeLinkEditText) EditText mLinkEditText;
    @BindView(R.id.spinner_rental_period) Spinner mRentalSpinner;
    @BindView(R.id.btn_select_video) Button mBtnSelectVideo;

    private static final String TAG = AddMovieActivity.class.getSimpleName();
    private YouTubeApi mService;
    private YoutubeMovie mYoutubeMovie;
    private Item mYoutubeItem;
    private String mYoutubeId;
    private Uri mYoutubeUri;
    private Statistics mMovieStats;

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
    }

    private void buildUrlFromUserEntry() {
        if (!TextUtils.isEmpty(mLinkEditText.getText().toString())) {
            String inputString = mLinkEditText.getText().toString();

            mYoutubeUri = MyLinkUtils.buildUriFromString(inputString);
            mYoutubeId = MyLinkUtils.getYoutubeIdFromLink(inputString);
        }
        mLinkEditText.setText("");
    }

    private void selectVideo() {

        if (mYoutubeId == null) return;

        if (mService == null) {
            mService = YouTubeApiClient.getClient().create(YouTubeApi.class);
        }

        final Call<YoutubeMovie> callMovie = mService.getMovieStatistics(
                mYoutubeId,
                BuildConfig.YOUTUBE_API_KEY,
                "statistics,snippet");
        callMovie.enqueue(new Callback<YoutubeMovie>() {
            @Override
            public void onResponse(Call<YoutubeMovie> call, Response<YoutubeMovie> response) {
                mYoutubeMovie = response.body();

                Log.v(TAG, "Got something! " + callMovie.request().url());

                addMovieToDatabase();
                finish();
            }

            @Override
            public void onFailure(Call<YoutubeMovie> call, Throwable t) {
                Log.v(TAG, "Hmm, something went wrong " + callMovie.request().url());
                t.printStackTrace();
            }
        });
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

    private void addMovieToDatabase() {

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

        Uri newUri = getContentResolver().insert(
                TinyDbContract.TinyDbEntry.CONTENT_URI,
                values);
        Log.v(TAG, "New movie rented! " + newUri);
    }
}
