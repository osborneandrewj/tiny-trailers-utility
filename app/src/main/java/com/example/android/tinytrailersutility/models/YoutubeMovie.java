package com.example.android.tinytrailersutility.models;

import java.io.Serializable;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class YoutubeMovie implements Serializable, Parcelable
{

    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("etag")
    @Expose
    private String etag;
    @SerializedName("pageInfo")
    @Expose
    private PageInfo pageInfo;
    @SerializedName("items")
    @Expose
    private List<Item> items = null;
    public final static Parcelable.Creator<YoutubeMovie> CREATOR = new Creator<YoutubeMovie>() {


        @SuppressWarnings({
                "unchecked"
        })
        public YoutubeMovie createFromParcel(Parcel in) {
            YoutubeMovie instance = new YoutubeMovie();
            instance.kind = ((String) in.readValue((String.class.getClassLoader())));
            instance.etag = ((String) in.readValue((String.class.getClassLoader())));
            instance.pageInfo = ((PageInfo) in.readValue((PageInfo.class.getClassLoader())));
            in.readList(instance.items, (com.example.android.tinytrailersutility.models.Item.class.getClassLoader()));
            return instance;
        }

        public YoutubeMovie[] newArray(int size) {
            return (new YoutubeMovie[size]);
        }

    }
            ;
    private final static long serialVersionUID = -7479304052588606224L;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(kind);
        dest.writeValue(etag);
        dest.writeValue(pageInfo);
        dest.writeList(items);
    }

    public int describeContents() {
        return 0;
    }

}