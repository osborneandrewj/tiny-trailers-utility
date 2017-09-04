package com.example.android.tinytrailersutility.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.tinytrailersutility.R;
import com.example.android.tinytrailersutility.database.TinyDbContract;
import com.example.android.tinytrailersutility.utilities.MyTimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zark on 8/21/2017.\
 *
 */

public class TinyMovieLightAdapter extends RecyclerView.Adapter<TinyMovieLightAdapter.TinyMovieLightAdapterViewHolder>{

    private Cursor mCursor;
    private Context mContext;
    private LayoutInflater mInflater;

    public TinyMovieLightAdapter(Context context,
                                 Cursor cursor) {
        mInflater = LayoutInflater.from(context);
        mCursor = cursor;
    }

    @Override
    public TinyMovieLightAdapter.TinyMovieLightAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = mInflater.inflate(R.layout.item_tiny_movie, parent, false);
        final TinyMovieLightAdapterViewHolder viewHolder = new TinyMovieLightAdapterViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TinyMovieLightAdapter.TinyMovieLightAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.mYoutubeNameTextView.setText(mCursor.getString(
                mCursor.getColumnIndexOrThrow(TinyDbContract.TinyDbEntry.COLUMN_MOVIE_NAME)));
        //holder.mStartTimeTextView.setText(MyTimeUtils.getFormattedTimeFromMillis(Long.valueOf(mCursor.getString(
        //        mCursor.getColumnIndexOrThrow(TinyDbContract.TinyDbEntry.COLUMN_START_TIME)))));
        holder.mMinutesElapsedTextView.setText(MyTimeUtils.getMinutesElapsedSinceTimeStamp(Long.valueOf(mCursor.getString(
                mCursor.getColumnIndexOrThrow(TinyDbContract.TinyDbEntry.COLUMN_START_TIME)))));
//        holder.mRentalPeriodTextView.setText(mCursor.getString(
//                mCursor.getColumnIndexOrThrow(TinyDbContract.TinyDbEntry.COLUMN_RENTAL_LENGTH)));
//        holder.mStartingViewsTextView.setText(mCursor.getString(
//                mCursor.getColumnIndexOrThrow(TinyDbContract.TinyDbEntry.COLUMN_STARTING_VIEWS)));
//        holder.mCurrentViewsTextView.setText(mCursor.getString(
//                mCursor.getColumnIndexOrThrow(TinyDbContract.TinyDbEntry.COLUMN_CURRENT_VIEWS)));
        holder.mTicketsSoldTextView.setText(mCursor.getString(
                mCursor.getColumnIndexOrThrow(TinyDbContract.TinyDbEntry.COLUMN_TICKETS_SOLD)));
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    public class TinyMovieLightAdapterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_youtube_name) TextView mYoutubeNameTextView;
        //@BindView(R.id.tv_start_time) TextView mStartTimeTextView;
        //@BindView(R.id.tv_rental_period) TextView mRentalPeriodTextView;
        @BindView(R.id.tv_minutes_elapsed) TextView mMinutesElapsedTextView;
        //@BindView(R.id.tv_starting_views) TextView mStartingViewsTextView;
        //@BindView(R.id.tv_current_views) TextView mCurrentViewsTextView;
        @BindView(R.id.tv_tickets_sold) TextView mTicketsSoldTextView;

        public TinyMovieLightAdapterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setTinyMovieData(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }
}
