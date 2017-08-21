package com.example.android.tinytrailersutility.database;

import com.orm.SugarRecord;
import com.orm.dsl.Table;
import com.orm.dsl.Unique;

/**
 * Created by zark on 8/19/17.
 *
 */

@Table
public class TinyMovie extends SugarRecord {

    @Unique
    String mUri;
    String mRentalLength;
    String mStartTime;
    String mStartingViews;
    String mCurrentViews;


    /**
     * Default constructor is necessary here
     */
    public TinyMovie() {}

    public TinyMovie (String uri,
                      String rentalLength,
                      String startTime,
                      String startingViews,
                      String currentViews) {
        mUri = uri;
        mRentalLength = rentalLength;
        mStartTime = startTime;
        mStartingViews = startingViews;
        mCurrentViews = currentViews;
    }

    public String geturi() {
        return mUri;
    }

}
