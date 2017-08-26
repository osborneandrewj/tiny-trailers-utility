package com.example.android.tinytrailersutility.services;

import com.squareup.otto.Bus;

/**
 * Created by zark on 8/25/17.
 *
 */

public class BusProvider {

    private static Bus sBus;

    public static Bus getBus() {

        if (sBus == null) {
            sBus = new Bus();
        }

        return sBus;
    }
}
