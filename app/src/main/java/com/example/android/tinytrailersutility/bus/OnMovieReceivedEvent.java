package com.example.android.tinytrailersutility.bus;

import com.example.android.tinytrailersutility.models.youtube.YoutubeMovie;

/**
 * Created by Zark on 8/29/2017.
 *
 */

public class OnMovieReceivedEvent {

    public final YoutubeMovie mNewMovie;

    public OnMovieReceivedEvent(YoutubeMovie aNewMovie) {
        mNewMovie = aNewMovie;
    }
}
