package com.example.android.tinytrailersutility.models;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PageInfo implements Serializable, Parcelable
{

    @SerializedName("totalResults")
    @Expose
    private Integer totalResults;
    @SerializedName("resultsPerPage")
    @Expose
    private Integer resultsPerPage;
    public final static Parcelable.Creator<PageInfo> CREATOR = new Creator<PageInfo>() {


        @SuppressWarnings({
                "unchecked"
        })
        public PageInfo createFromParcel(Parcel in) {
            PageInfo instance = new PageInfo();
            instance.totalResults = ((Integer) in.readValue((Integer.class.getClassLoader())));
            instance.resultsPerPage = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }

        public PageInfo[] newArray(int size) {
            return (new PageInfo[size]);
        }

    }
            ;
    private final static long serialVersionUID = 2625231596771470544L;

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Integer getResultsPerPage() {
        return resultsPerPage;
    }

    public void setResultsPerPage(Integer resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(totalResults);
        dest.writeValue(resultsPerPage);
    }

    public int describeContents() {
        return 0;
    }

}