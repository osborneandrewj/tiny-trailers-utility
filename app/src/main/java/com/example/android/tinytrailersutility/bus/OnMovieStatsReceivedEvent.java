package com.example.android.tinytrailersutility.bus;

import com.example.android.tinytrailersutility.models.youtube.YoutubeMovie;

/**
 * Created by Zark on 8/29/2017.
 *
 */

public class OnMovieStatsReceivedEvent {

    public final YoutubeMovie mNewMovie;

    public OnMovieStatsReceivedEvent(YoutubeMovie aNewMovie) {
        mNewMovie = aNewMovie;
    }
}
