package com.example.android.tinytrailersutility.utilities;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Zark on 8/21/2017.
 *
 */

public class MyTimeUtils {

    public static String getFormattedTimeFromMillis(long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd H:mm", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT-7"));
        return format.format(timestamp);
    }

    public static String getMinutesElapsedSinceTimeStamp(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - timestamp;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeElapsed);

        return String.valueOf(minutes);
    }

    public static long getTimeLeftInMillis(String startTimeString, String rentalLengthString) {
        long startTime = Long.valueOf(startTimeString);
        long rentalLength = TimeUnit.MINUTES.toMillis(Long.valueOf(rentalLengthString));
        long currentTime = System.currentTimeMillis();

        long timeLeft = rentalLength - (currentTime - startTime);

        if (timeLeft <= 0) {
            timeLeft = 0;
        }

        return timeLeft;
    }
}
