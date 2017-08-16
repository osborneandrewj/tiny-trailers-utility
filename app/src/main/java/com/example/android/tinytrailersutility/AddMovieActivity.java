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

import com.example.android.tinytrailersutility.models.Item;
import com.example.android.tinytrailersutility.models.Movie;
import com.example.android.tinytrailersutility.models.Statistics;
import com.example.android.tinytrailersutility.rest.YouTubeApi;
import com.example.android.tinytrailersutility.rest.YouTubeApiClient;
import com.example.android.tinytrailersutility.utilities.MyNetworkUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMovieActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    @BindView(R.id.youtubeLinkEditText) EditText mLinkEditText;
    @BindView(R.id.spinner_rental_period) Spinner mRentalSpinner;
    @BindView(R.id.btn_book) Button mBtnBook;

    private YouTubeApi mService;


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

        mBtnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMovie();
            }
        });
    }

    private void addNewMovie() {
        String userEntry;
        Uri uri = null;
        if (mLinkEditText != null && !TextUtils.isEmpty(mLinkEditText.getText())) {
            userEntry = mLinkEditText.getText().toString();
            uri = MyNetworkUtils.buildUriFromString(userEntry);
        }

        String movieId = "Ks-_Mh1QhMc";
        String part = "statistics";

        if (mService == null) {
            mService = YouTubeApiClient.getClient().create(YouTubeApi.class);
        }

        final Call<Movie> callMovie = mService.getMovieStatistics(movieId, BuildConfig.YOUTUBE_API_KEY,
                part);
        callMovie.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movie = response.body();
                Log.v("TAG", "Got something! " + callMovie.request().url() + " " +
                movie.getKind());
                Item item = movie.getItems().get(0);
                Statistics statistics = item.getStatistics();
                Log.v("TAG", "views: " + statistics.getViewCount());
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.v("TAG", "Hmm, something went wrong " + callMovie.request().url());
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
}
