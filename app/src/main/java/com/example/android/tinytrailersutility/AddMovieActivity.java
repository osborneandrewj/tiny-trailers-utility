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
import android.widget.Toast;

import com.example.android.tinytrailersutility.database.TinyMovie;
import com.example.android.tinytrailersutility.models.Item;
import com.example.android.tinytrailersutility.models.YoutubeMovie;
import com.example.android.tinytrailersutility.models.Statistics;
import com.example.android.tinytrailersutility.rest.YouTubeApi;
import com.example.android.tinytrailersutility.rest.YouTubeApiClient;
import com.example.android.tinytrailersutility.utilities.MyLinkUtils;
import com.orm.SugarContext;
import com.orm.SugarRecord;

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
    private String mId;
    private Uri mUri;
    private String mUriString;
    private SugarRecord mSugarRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);
        ButterKnife.bind(this);

        SugarContext.init(this);

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
                addNewMovie();
            }
        });
    }

    private void buildUrlFromUserEntry() {
        if (!TextUtils.isEmpty(mLinkEditText.getText().toString())) {
            String inputString = mLinkEditText.getText().toString();

            mUri = MyLinkUtils.buildUriFromString(inputString);
            mId = MyLinkUtils.getYoutubeIdFromLink(inputString);
        }
        mLinkEditText.setText("");
    }

    private void addNewMovie() {
        String part = "statistics";

        if (mId == null) {
            return;
        }

        if (mService == null) {
            mService = YouTubeApiClient.getClient().create(YouTubeApi.class);
        }

        final Call<YoutubeMovie> callMovie = mService.getMovieStatistics(mId, BuildConfig.YOUTUBE_API_KEY,
                part);
        callMovie.enqueue(new Callback<YoutubeMovie>() {
            @Override
            public void onResponse(Call<YoutubeMovie> call, Response<YoutubeMovie> response) {
                YoutubeMovie youtubeMovie = response.body();
                Log.v(TAG, "Got something! " + callMovie.request().url() + " " +
                youtubeMovie.getKind());
                Item item = youtubeMovie.getItems().get(0);
                Statistics statistics = item.getStatistics();
                String viewCount = statistics.getViewCount();
                Log.v("TAG", "views: " + statistics.getViewCount());
                Toast.makeText(AddMovieActivity.this, viewCount, Toast.LENGTH_SHORT).show();

                TinyMovie newMovie = new TinyMovie(mUri.toString(),
                        "3",
                        "9",
                        statistics.getViewCount(),
                        statistics.getViewCount());
                SugarRecord.save(newMovie);


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
        SugarContext.terminate();
    }
}
