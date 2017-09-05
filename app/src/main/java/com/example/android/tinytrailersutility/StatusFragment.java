package com.example.android.tinytrailersutility;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String CASH_TOTAL_KEY = "cash-total-key";
    private SharedPreferences mSharedPrefs;

    @BindView(R.id.tv_cash_total) TextView mCashTotal;


    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_status, container, false);
        ButterKnife.bind(this, view);
        mSharedPrefs = getContext().getSharedPreferences(
                "tinytrailerutilityScreenSettings",
                Context.MODE_PRIVATE);
        int cash = mSharedPrefs.getInt(CASH_TOTAL_KEY, 0);
        mCashTotal.setText(String.valueOf(cash));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        mCashTotal.setText(String.valueOf(mSharedPrefs.getInt(CASH_TOTAL_KEY, 0)));
    }
}
