package com.example.android.tinytrailersutility.bus;

/**
 * Created by Zark on 9/4/2017.
 *
 */

public class OnMovieAttachedToScreenEvent {

    public final String mId;
    public final String mScreenKey;

    public OnMovieAttachedToScreenEvent(String aId, String aScreenKey) {
        mId = aId;
        mScreenKey = aScreenKey;
    }
}
