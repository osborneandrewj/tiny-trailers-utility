package com.example.android.tinytrailersutility.utilities;

/**
 * Created by Zark on 8/29/2017.
 *
 */

public final class MyTicketUtilities {

    public static int getNumberOfTicketsSold(String startingViews, String currentViews) {
        int startingCount = Integer.parseInt(startingViews);
        int currentCount = Integer.parseInt(currentViews);

        return currentCount - startingCount;
    }

}
