package com.udacity.lineker.themoviedb.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Trailer implements Parcelable{
    private String key;
    private String urlImage;
    private String title;

    public Trailer() {

    }

    public Trailer(String key, String urlImage, String title) {
        this.setKey(key);
        this.setUrlImage(urlImage);
        this.setTitle(title);
    }

    private Trailer(Parcel p){
        this.setKey(p.readString());
        this.setUrlImage(p.readString());
        this.setTitle(p.readString());
    }

    public static final Parcelable.Creator<Trailer>
            CREATOR = new Parcelable.Creator<Trailer>() {

        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.getKey());
        dest.writeString(this.getUrlImage());
        dest.writeString(this.getTitle());
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
