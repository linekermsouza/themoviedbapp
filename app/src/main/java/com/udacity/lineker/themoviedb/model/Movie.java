package com.udacity.lineker.themoviedb.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    private int id;
    private String posterPath;
    private String backdropPath;
    private String title;
    private String genre;
    private String rate;
    private String vote;
    private String runtime;
    private String release;
    private String summary;

    public Movie() {

    }

    public Movie(int id, String posterPath, String backdropPath)
    {
        this.setId(id);
        this.setPosterPath(posterPath);
        this.setBackdropPath(backdropPath);
    }

    private Movie(Parcel p){
        id = p.readInt();
        posterPath = p.readString();
        backdropPath = p.readString();
        title = p.readString();
        genre = p.readString();
        rate = p.readString();
        vote = p.readString();
        runtime = p.readString();
        release = p.readString();
        summary = p.readString();
    }

    public static final Parcelable.Creator<Movie>
            CREATOR = new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeString(title);
        dest.writeString(genre);
        dest.writeString(rate);
        dest.writeString(vote);
        dest.writeString(runtime);
        dest.writeString(release);
        dest.writeString(summary);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getVote() {
        return vote;
    }

    public void setVote(String vote) {
        this.vote = vote;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}