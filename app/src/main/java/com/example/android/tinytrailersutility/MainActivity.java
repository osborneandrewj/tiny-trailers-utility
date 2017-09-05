package com.example.android.tinytrailersutility;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;


import com.example.android.tinytrailersutility.bus.BusProvider;
import com.example.android.tinytrailersutility.bus.OnMovieStatsReceivedEvent;
import com.example.android.tinytrailersutility.services.MovieService;
import com.example.android.tinytrailersutility.rest.YouTubeApi;
import com.example.android.tinytrailersutility.rest.YouTubeApiClient;
import com.example.android.tinytrailersutility.services.DatabaseService;
import com.example.android.tinytrailersutility.utilities.FirebaseJobUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Red camera icon by: Hanan from flaticon.com
 * Tickets launcher icon by: Dimi Kazak from flaticon.com
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SCREEN_EXTRA_KEY = "screen-extra-key";
    private static final String SCREEN_ONE_KEY = "screen-one-key";
    private static final String SCREEN_TWO_KEY = "screen-two-key";
    private static final String CASH_TOTAL_KEY = "cash-total-key";
    private YouTubeApi mService;
    private DatabaseService mDatabaseService;
    private MovieService mMovieService;
    private Bus mBus = BusProvider.getInstance();
    private String mYouTubeId;
    private String mWhichScreen;

    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.container_screen_one) FrameLayout mScreenOne;
    @BindView(R.id.container_screen_two) ConstraintLayout mScreenTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddMovieActivity("0");
            }
        });

        mScreenOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddMovieActivity(SCREEN_ONE_KEY);
            }
        });

        mScreenTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddMovieActivity(SCREEN_TWO_KEY);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        if (mMovieService == null) mMovieService = new MovieService(buildApi(), mBus);
        if (mDatabaseService == null) mDatabaseService = new DatabaseService(this, mBus);

        // Add the status bar fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        StatusFragment statusFragment = new StatusFragment();
        ScreenOneFragment screenOneFragment = new ScreenOneFragment();
        fragmentTransaction.add(R.id.container_status, statusFragment);
        fragmentTransaction.add(R.id.container_screen_one, screenOneFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);

        SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        mYouTubeId = sharedPrefs.getString(getString(R.string.preference_screen_one_key), "-1");


    }

    public void deleteMovies() {
        mDatabaseService.deleteEntireDatabase();
    }

    public void openAddMovieActivity(String aScreen) {
        Intent intent = new Intent(this, AddMovieActivity.class);
        intent.putExtra(SCREEN_EXTRA_KEY, aScreen);
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
        }
        if (id == R.id.action_delete_movies) {
            deleteMovies();
            FirebaseJobUtils.cancelAllJobs(this);
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
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
