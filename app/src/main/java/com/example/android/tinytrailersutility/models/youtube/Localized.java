package com.example.android.tinytrailersutility.models.youtube;

import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Localized implements Serializable, Parcelable
{

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    public final static Parcelable.Creator<Localized> CREATOR = new Creator<Localized>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Localized createFromParcel(Parcel in) {
            Localized instance = new Localized();
            instance.title = ((String) in.readValue((String.class.getClassLoader())));
            instance.description = ((String) in.readValue((String.class.getClassLoader())));
            return instance;
        }

        public Localized[] newArray(int size) {
            return (new Localized[size]);
        }

    }
            ;
    private final static long serialVersionUID = 6767230893712151490L;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(title);
        dest.writeValue(description);
    }

    public int describeContents() {
        return 0;
    }

}