package com.example.android.tinytrailersutility;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String CASH_TOTAL_KEY = "cash-total-key";
    private static final int PREFERENCES_ERROR = -1;
    private SharedPreferences mSharedPrefs;
    private int mCashTotal;

    @BindView(R.id.tv_cash_total) TextView mCashTotalTextView;
    @BindView(R.id.btn_test_status) Button mTestButton;


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
        mCashTotal = mSharedPrefs.getInt(CASH_TOTAL_KEY, PREFERENCES_ERROR);
        mCashTotalTextView.setText(String.valueOf(mCashTotal));

        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mSharedPrefs.edit();
                editor.putInt(CASH_TOTAL_KEY, mCashTotal+1000);
                editor.commit();
            }
        });

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
        mCashTotalTextView.setText(String.valueOf(mSharedPrefs.getInt(CASH_TOTAL_KEY, PREFERENCES_ERROR)));
    }
}
