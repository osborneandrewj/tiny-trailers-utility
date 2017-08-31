package com.example.android.tinytrailersutility;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


import com.example.android.tinytrailersutility.adapters.TinyMovieLightAdapter;
import com.example.android.tinytrailersutility.bus.BusProvider;
import com.example.android.tinytrailersutility.bus.OnMovieStatsReceivedEvent;
import com.example.android.tinytrailersutility.database.TinyDbContract;
import com.example.android.tinytrailersutility.models.youtube.YoutubeMovie;
import com.example.android.tinytrailersutility.services.MovieService;
import com.example.android.tinytrailersutility.rest.YouTubeApi;
import com.example.android.tinytrailersutility.rest.YouTubeApiClient;
import com.example.android.tinytrailersutility.services.DatabaseService;
import com.example.android.tinytrailersutility.utilities.FirebaseJobUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Red camera icon by: Hanan from flaticon.com
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int UNIQUE_ID_FOR_LOADER = 1986;
    private TinyMovieLightAdapter mMovieLightAdapter;
    private YouTubeApi mService;
    private DatabaseService mDatabaseService;
    private MovieService mMovieService;
    private Bus mBus = BusProvider.getInstance();

    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.rv_tiny_movies) RecyclerView mTinyMovieRecyclerView;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddMovieActivity();
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        mTinyMovieRecyclerView.setHasFixedSize(true);
        mMovieLightAdapter = new TinyMovieLightAdapter(this, null);
        mTinyMovieRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTinyMovieRecyclerView.setAdapter(mMovieLightAdapter);

        getSupportLoaderManager().initLoader(UNIQUE_ID_FOR_LOADER, null, this);

        if (mMovieService == null) mMovieService = new MovieService(buildApi(), mBus);
        if (mDatabaseService == null) mDatabaseService = new DatabaseService(this, mBus);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);
    }

    public void refreshData() {
        ArrayList<String> idList = mDatabaseService.getYouTubeIdsFromLocalMovies(this);
        Log.v(TAG, "Size of list: " + idList.size());
        for (String id : idList) {
            mMovieService.getMovieStatistics(id);
        }
    }

    public void testRefreshData() {
        ArrayList<String> idList = mDatabaseService.getYouTubeIdsFromLocalMovies(this);
        String idString = android.text.TextUtils.join(",", idList);

        final Call<YoutubeMovie> updateMoviesSilently = buildApi()
                .getMultipleMovieDetails(
                        idString,
                        MovieService.mKey,
                        MovieService.mStatistics);
        updateMoviesSilently.enqueue(new Callback<YoutubeMovie>() {
            @Override
            public void onResponse(Call<YoutubeMovie> call, Response<YoutubeMovie> response) {
                Log.v("TAG", "Response: " + call.request().url());
                mDatabaseService.updateLocalMoviesWithNewData(response.body());
            }

            @Override
            public void onFailure(Call<YoutubeMovie> call, Throwable t) {

            }
        });
    }

    public void deleteMovies() {
        mDatabaseService.deleteEntireDatabase();
    }

    public void openAddMovieActivity() {
        Intent intent = new Intent(this, AddMovieActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_refresh_data) {
            refreshData();
        }
        if (id == R.id.action_delete_movies) {
            deleteMovies();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            FirebaseJobUtils.cancelAllJobs(this);
        } else if (id == R.id.nav_gallery) {
            FirebaseJobUtils.scheduleMovieUpdate(this);
        } else if (id == R.id.nav_slideshow) {
            testRefreshData();
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String [] projection = {
                TinyDbContract.TinyDbEntry._ID,
                TinyDbContract.TinyDbEntry.COLUMN_MOVIE_YOUTUBE_ID,
                TinyDbContract.TinyDbEntry.COLUMN_MOVIE_URI,
                TinyDbContract.TinyDbEntry.COLUMN_MOVIE_NAME,
                TinyDbContract.TinyDbEntry.COLUMN_RENTAL_LENGTH,
                TinyDbContract.TinyDbEntry.COLUMN_START_TIME,
                TinyDbContract.TinyDbEntry.COLUMN_STARTING_VIEWS,
                TinyDbContract.TinyDbEntry.COLUMN_CURRENT_VIEWS,
                TinyDbContract.TinyDbEntry.COLUMN_TICKETS_SOLD};
        return new CursorLoader(this,
                TinyDbContract.TinyDbEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieLightAdapter.setTinyMovieData(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private YouTubeApi buildApi() {
        if (mService == null) {
            mService = YouTubeApiClient.getClient().create(YouTubeApi.class);
            return mService;
        } else return mService;
    }

    @Subscribe
    public void onNewMovieStatsReceived(OnMovieStatsReceivedEvent event) {
        mDatabaseService.updateTinyMovieViews(event.mNewMovie);
        mDatabaseService.updateLocalMoviesWithNewData(event.mNewMovie);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }
}
